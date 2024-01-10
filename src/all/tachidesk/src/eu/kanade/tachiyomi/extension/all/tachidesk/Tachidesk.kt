package eu.kanade.tachiyomi.extension.all.tachidesk

import android.app.Application
import android.content.SharedPreferences
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceScreen
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.asObservableSuccess
import eu.kanade.tachiyomi.source.ConfigurableSource
import eu.kanade.tachiyomi.source.UnmeteredSource
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Dns
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.toImmutableList
import rx.Observable
import rx.Single
import rx.schedulers.Schedulers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy
import java.util.concurrent.TimeUnit
import kotlin.math.min

class Tachidesk : ConfigurableSource, UnmeteredSource, HttpSource() {
    override val name = "Suwayomi"
    override val id = 3100117499901280806L
    override val baseUrl by lazy { getPrefBaseUrl() }
    private val baseLogin by lazy { getPrefBaseLogin() }
    private val basePassword by lazy { getPrefBasePassword() }

    override val lang = "all"
    override val supportsLatest = false

    private val json: Json by injectLazy()

    override val client: OkHttpClient =
        network.client.newBuilder()
            .dns(Dns.SYSTEM) // don't use DNS over HTTPS as it breaks IP addressing
            .callTimeout(2, TimeUnit.MINUTES)
            .build()

    override fun headersBuilder(): Headers.Builder = Headers.Builder().apply {
        if (basePassword.isNotEmpty() && baseLogin.isNotEmpty()) {
            val credentials = Credentials.basic(baseLogin, basePassword)
            add("Authorization", credentials)
        }
    }

    // ------------- Popular Manga -------------

    // Route the popular manga view through search to avoid duplicate code path
    override fun popularMangaRequest(page: Int): Request =
        searchMangaRequest(page, "", FilterList())

    override fun popularMangaParse(response: Response): MangasPage =
        searchMangaParse(response)

    // ------------- Manga Details -------------

    override fun mangaDetailsRequest(manga: SManga) =
        GET("$checkedBaseUrl/api/v1/manga/${manga.url}/?onlineFetch=true", headers)

    override fun mangaDetailsParse(response: Response): SManga =
        json.decodeFromString<MangaDataClass>(response.body.string()).toSManga()

    // ------------- Chapter -------------

    override fun chapterListRequest(manga: SManga): Request =
        GET("$checkedBaseUrl/api/v1/manga/${manga.url}/chapters?onlineFetch=true", headers)

    override fun chapterListParse(response: Response): List<SChapter> =
        json.decodeFromString<List<ChapterDataClass>>(response.body.string()).map {
            it.toSChapter()
        }

    // ------------- Page List -------------

    override fun fetchPageList(chapter: SChapter): Observable<List<Page>> {
        return client.newCall(pageListRequest(chapter))
            .asObservableSuccess()
            .map { response ->
                pageListParse(response, chapter)
            }
    }

    override fun pageListRequest(chapter: SChapter): Request {
        val mangaId = chapter.url.split(" ").first()
        val chapterIndex = chapter.url.split(" ").last()

        return GET("$checkedBaseUrl/api/v1/manga/$mangaId/chapter/$chapterIndex/?onlineFetch=True", headers)
    }

    private fun pageListParse(response: Response, sChapter: SChapter): List<Page> {
        val mangaId = sChapter.url.split(" ").first()
        val chapterIndex = sChapter.url.split(" ").last()

        val chapter = json.decodeFromString<ChapterDataClass>(response.body.string())

        return List(chapter.pageCount) {
            Page(it + 1, "", "$checkedBaseUrl/api/v1/manga/$mangaId/chapter/$chapterIndex/page/$it/")
        }
    }

    // ------------- Filters & Search -------------

    private var categoryList: List<CategoryDataClass> = emptyList()
    private val defaultCategoryId: Int
        get() = categoryList.firstOrNull()?.id ?: 0

    private val resultsPerPageOptions = listOf(10, 15, 20, 25)
    private val defaultResultsPerPage = resultsPerPageOptions.first()

    private val sortByOptions = listOf(
        "Title",
        "Artist",
        "Author",
        "Date added",
        "Total chapters",
    )
    private val defaultSortByIndex = 0

    private var tagList: List<String> = emptyList()
    private val tagModeAndString = "AND"
    private val tagModeOrString = "OR"
    private val tagModes = listOf(tagModeAndString, tagModeOrString)
    private val defaultIncludeTagModeIndex = tagModes.indexOf(tagModeAndString)
    private val defaultExcludeTagModeIndex = tagModes.indexOf(tagModeOrString)
    private val tagFilterModeIncludeString = "Include"
    private val tagFilterModeExcludeString = "Exclude"

    class CategorySelect(categoryList: List<CategoryDataClass>) :
        Filter.Select<String>("Category", categoryList.map { it.name }.toTypedArray())

    class ResultsPerPageSelect(options: List<Int>) :
        Filter.Select<Int>("Results per page", options.toTypedArray())

    class SortBy(options: List<String>) :
        Filter.Sort(
            "Sort by",
            options.toTypedArray(),
            Selection(0, true),
        )

    class Tag(name: String, state: Int) :
        Filter.TriState(name, state)

    class TagFilterMode(type: String, tagModes: List<String>, defaultIndex: Int = 0) :
        Filter.Select<String>(type, tagModes.toTypedArray(), defaultIndex)

    class TagSelector(tagList: List<String>) :
        Filter.Group<Tag>(
            "Tags",
            tagList.map { tag -> Tag(tag, 0) },
        )

    class TagFilterModeGroup(
        tagModes: List<String>,
        includeString: String,
        excludeString: String,
        includeDefaultIndex: Int = 0,
        excludeDefaultIndex: Int = 0,
    ) :
        Filter.Group<TagFilterMode>(
            "Tag Filter Modes",
            listOf(
                TagFilterMode(includeString, tagModes, includeDefaultIndex),
                TagFilterMode(excludeString, tagModes, excludeDefaultIndex),
            ),
        )

    override fun getFilterList(): FilterList = FilterList(
        Filter.Header("Press reset to refresh tag list and attempt to fetch categories."),
        Filter.Header("Tag list shows only the tags of currently displayed manga."),
        Filter.Header("\"All\" shows all manga regardless of category."),
        CategorySelect(refreshCategoryList(baseUrl).let { categoryList }),
        Filter.Separator(),
        TagFilterModeGroup(
            tagModes,
            tagFilterModeIncludeString,
            tagFilterModeExcludeString,
            defaultIncludeTagModeIndex,
            defaultExcludeTagModeIndex,
        ),
        TagSelector(tagList),
        SortBy(sortByOptions),
        ResultsPerPageSelect(resultsPerPageOptions),
    )

    private fun refreshCategoryList(baseUrl: String) {
        Single.fromCallable {
            client.newCall(GET("$baseUrl/api/v1/category", headers)).execute()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { response ->
                    categoryList = try {
                        // Add a pseudo category to list all manga across all categories
                        listOf(CategoryDataClass(-1, -1, "All", false)) +
                            json.decodeFromString<List<CategoryDataClass>>(response.body.string())
                    } catch (e: Exception) {
                        emptyList()
                    }
                },
                {},
            )
    }

    private fun refreshTagList(mangaList: List<MangaDataClass>) {
        val newTagList = mutableListOf<String>()
        for (mangaDetails in mangaList) {
            newTagList.addAll(mangaDetails.genre)
        }
        tagList = newTagList
            .distinctBy { tag -> tag.lowercase() }
            .sortedBy { tag -> tag.lowercase() }
            .filter { tag -> tag.trim() != "" }
    }

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        // Embed search query and scope into URL params for processing in searchMangaParse
        var currentCategoryId = defaultCategoryId
        var resultsPerPage = defaultResultsPerPage
        var sortByIndex = defaultSortByIndex
        var sortByAscending = true
        val tagIncludeList = mutableListOf<String>()
        val tagExcludeList = mutableListOf<String>()
        var tagFilterIncludeModeIndex = defaultIncludeTagModeIndex
        var tagFilterExcludeModeIndex = defaultExcludeTagModeIndex
        filters.forEach { filter ->
            when (filter) {
                is CategorySelect -> currentCategoryId = categoryList[filter.state].id
                is ResultsPerPageSelect -> resultsPerPage = resultsPerPageOptions[filter.state]
                is SortBy -> {
                    sortByIndex = filter.state?.index ?: sortByIndex
                    sortByAscending = filter.state?.ascending ?: sortByAscending
                }
                is TagFilterModeGroup -> {
                    filter.state.forEach { tagFilterMode ->
                        when (tagFilterMode.name) {
                            tagFilterModeIncludeString -> tagFilterIncludeModeIndex = tagFilterMode.state
                            tagFilterModeExcludeString -> tagFilterExcludeModeIndex = tagFilterMode.state
                        }
                    }
                }
                is TagSelector -> {
                    filter.state.forEach { tagFilter ->
                        when {
                            tagFilter.isIncluded() -> tagIncludeList.add(tagFilter.name)
                            tagFilter.isExcluded() -> tagExcludeList.add(tagFilter.name)
                        }
                    }
                }
                else -> {}
            }
        }
        val url = "$checkedBaseUrl/api/v1/$currentCategoryId"
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("searchQuery", query)
            .addQueryParameter("currentCategoryId", currentCategoryId.toString())
            .addQueryParameter("sortBy", sortByIndex.toString())
            .addQueryParameter("sortByAscending", sortByAscending.toString())
            .addQueryParameter("tagFilterIncludeMode", tagFilterIncludeModeIndex.toString())
            .addQueryParameter("tagFilterExcludeMode", tagFilterExcludeModeIndex.toString())
            .addQueryParameter("tagIncludeList", tagIncludeList.joinToString(","))
            .addQueryParameter("tagExcludeList", tagExcludeList.joinToString(","))
            .addQueryParameter("resultsPerPage", resultsPerPage.toString())
            .addQueryParameter("page", page.toString())
            .build()
        return GET(url, headers)
    }

    override fun searchMangaParse(response: Response): MangasPage {
        val request = response.request
        var searchQuery: String? = ""
        var currentCategoryId = defaultCategoryId
        var sortByIndex = defaultSortByIndex
        var sortByAscending = true
        var tagIncludeList = mutableListOf<String>()
        var tagExcludeList = mutableListOf<String>()
        var tagFilterIncludeModeIndex = defaultIncludeTagModeIndex
        var tagFilterExcludeModeIndex = defaultExcludeTagModeIndex
        var resultsPerPage = defaultResultsPerPage
        var page = 1

        // Check if URL has query params and parse them
        if (!request.url.query.isNullOrEmpty()) {
            searchQuery = request.url.queryParameter("searchQuery")
            currentCategoryId = request.url.queryParameter("currentCategoryId")?.toIntOrNull() ?: currentCategoryId
            sortByIndex = request.url.queryParameter("sortBy")?.toIntOrNull() ?: sortByIndex
            sortByAscending = request.url.queryParameter("sortByAscending").toBoolean()
            tagIncludeList = request.url.queryParameter("tagIncludeList").let { param ->
                if (param is String && param.isNotEmpty()) {
                    param.split(",").toMutableList()
                } else {
                    tagIncludeList
                }
            }
            tagExcludeList = request.url.queryParameter("tagExcludeList").let { param ->
                if (param is String && param.isNotEmpty()) {
                    param.split(",").toMutableList()
                } else {
                    tagExcludeList
                }
            }
            tagFilterIncludeModeIndex = request.url.queryParameter("tagFilterIncludeMode")?.toIntOrNull() ?: tagFilterIncludeModeIndex
            tagFilterExcludeModeIndex = request.url.queryParameter("tagFilterExcludeMode")?.toIntOrNull() ?: tagFilterExcludeModeIndex
            resultsPerPage = request.url.queryParameter("resultsPerPage")?.toIntOrNull() ?: resultsPerPage
            page = request.url.queryParameter("page")?.toIntOrNull() ?: page
        }
        val sortByProperty = sortByOptions[sortByIndex]
        val tagFilterIncludeMode = tagModes[tagFilterIncludeModeIndex]
        val tagFilterExcludeMode = tagModes[tagFilterExcludeModeIndex]

        // Get URLs of categories to search
        val categoryUrlList = if (currentCategoryId == -1) {
            categoryList.map { category -> "$checkedBaseUrl/api/v1/category/${category.id}" }
        } else {
            listOfNotNull("$checkedBaseUrl/api/v1/category/$currentCategoryId")
        }

        // Construct a list of all manga in the required categories by querying each one
        val mangaList = mutableListOf<MangaDataClass>()
        categoryUrlList.forEach { categoryUrl ->
            val categoryMangaListRequest =
                GET(categoryUrl, headers)
            val categoryMangaListResponse =
                client.newCall(categoryMangaListRequest).execute()
            val categoryMangaListJson =
                categoryMangaListResponse.body.string()
            val categoryMangaList =
                json.decodeFromString<List<MangaDataClass>>(categoryMangaListJson)
            mangaList.addAll(categoryMangaList)
        }

        // Filter by tags
        var searchResults = mangaList.toImmutableList()
        val filterConfigs = mutableListOf<Triple<Boolean, String, List<String>>>()
        if (tagExcludeList.isNotEmpty()) filterConfigs.add(Triple(false, tagFilterExcludeMode, tagExcludeList))
        if (tagIncludeList.isNotEmpty()) filterConfigs.add(Triple(true, tagFilterIncludeMode, tagIncludeList))
        filterConfigs.forEach { config ->
            val isInclude = config.first
            val filterMode = config.second
            val filteredTagList = config.third
            searchResults = searchResults.filter { mangaData ->
                val lowerCaseTags = mangaData.genre.map { it.lowercase() }
                val filterResult = when (filterMode) {
                    tagModeAndString -> lowerCaseTags.containsAll(filteredTagList.map { tag -> tag.lowercase() })
                    tagModeOrString -> lowerCaseTags.any { tag -> tag in filteredTagList.map { tag -> tag.lowercase() } }
                    else -> false
                }
                if (isInclude) filterResult else !filterResult
            }
        }

        // Filter according to search terms.
        // Basic substring search, room for improvement.
        searchResults = if (!searchQuery.isNullOrEmpty()) {
            searchResults.filter { mangaData ->
                val fieldsToCheck = listOfNotNull(
                    mangaData.title,
                    mangaData.url,
                    mangaData.artist,
                    mangaData.author,
                    mangaData.description,
                )
                fieldsToCheck.any { field ->
                    field.contains(searchQuery, ignoreCase = true)
                }
            }
        } else {
            searchResults
        }.distinct()

        // Sort results
        searchResults = when (sortByProperty) {
            "Title" -> searchResults.sortedBy { it.title }
            "Artist" -> searchResults.sortedBy { it.artist }
            "Author" -> searchResults.sortedBy { it.author }
            "Date added" -> searchResults.sortedBy { it.inLibraryAt }
            "Total chapters" -> searchResults.sortedBy { it.chapterCount }
            else -> searchResults
        }
        if (!sortByAscending) {
            searchResults = searchResults.asReversed()
        }

        // Get new list of tags from the search results
        refreshTagList(searchResults)

        // Paginate results
        val hasNextPage: Boolean
        with(paginateResults(searchResults, page, resultsPerPage)) {
            searchResults = first
            hasNextPage = second
        }

        return MangasPage(searchResults.map { mangaData -> mangaData.toSManga() }, hasNextPage)
    }

    // ------------- Images -------------
    override fun imageRequest(page: Page) = GET(page.imageUrl!!, headers)

    // ------------- Settings -------------

    private val preferences: SharedPreferences by lazy {
        Injekt.get<Application>().getSharedPreferences("source_$id", 0x0000)
    }

    init {
        val preferencesMap = mapOf(
            ADDRESS_TITLE to ADDRESS_DEFAULT,
            LOGIN_TITLE to LOGIN_DEFAULT,
            PASSWORD_TITLE to PASSWORD_DEFAULT,
        )

        preferencesMap.forEach { (key, defaultValue) ->
            val initBase = preferences.getString(key, defaultValue)!!

            if (initBase.isNotBlank()) {
                refreshCategoryList(initBase)
            }
        }
    }

    // ------------- Preferences -------------
    override fun setupPreferenceScreen(screen: PreferenceScreen) {
        screen.addPreference(screen.editTextPreference(ADDRESS_TITLE, ADDRESS_DEFAULT, baseUrl, false, "i.e. http://192.168.1.115:4567"))
        screen.addPreference(screen.editTextPreference(LOGIN_TITLE, LOGIN_DEFAULT, baseLogin, false, ""))
        screen.addPreference(screen.editTextPreference(PASSWORD_TITLE, PASSWORD_DEFAULT, basePassword, true, ""))
    }

    /** boilerplate for [EditTextPreference] */
    private fun PreferenceScreen.editTextPreference(title: String, default: String, value: String, isPassword: Boolean = false, placeholder: String): EditTextPreference {
        return EditTextPreference(context).apply {
            key = title
            this.title = title
            summary = value.ifEmpty { placeholder }
            this.setDefaultValue(default)
            dialogTitle = title

            if (isPassword) {
                setOnBindEditTextListener {
                    it.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            }

            setOnPreferenceChangeListener { _, newValue ->
                try {
                    val res = preferences.edit().putString(title, newValue as String).commit()
                    Toast.makeText(context, "Restart Tachiyomi to apply new setting.", Toast.LENGTH_LONG).show()
                    res
                } catch (e: Exception) {
                    Log.e("Tachidesk", "Exception while setting text preference", e)
                    false
                }
            }
        }
    }

    private fun getPrefBaseUrl(): String = preferences.getString(ADDRESS_TITLE, ADDRESS_DEFAULT)!!
    private fun getPrefBaseLogin(): String = preferences.getString(LOGIN_TITLE, LOGIN_DEFAULT)!!
    private fun getPrefBasePassword(): String = preferences.getString(PASSWORD_TITLE, PASSWORD_DEFAULT)!!

    companion object {
        private const val ADDRESS_TITLE = "Server URL Address"
        private const val ADDRESS_DEFAULT = ""
        private const val LOGIN_TITLE = "Login (Basic Auth)"
        private const val LOGIN_DEFAULT = ""
        private const val PASSWORD_TITLE = "Password (Basic Auth)"
        private const val PASSWORD_DEFAULT = ""
    }

    // ------------- Not Used -------------

    override fun latestUpdatesRequest(page: Int): Request = throw Exception("Not used")

    override fun latestUpdatesParse(response: Response): MangasPage = throw Exception("Not used")

    override fun pageListParse(response: Response): List<Page> = throw Exception("Not used")

    override fun imageUrlParse(response: Response): String = throw Exception("Not used")

    // ------------- Util -------------

    private fun MangaDataClass.toSManga() = SManga.create().also {
        it.url = id.toString()
        it.title = title
        it.thumbnail_url = "$baseUrl$thumbnailUrl"
        it.artist = artist
        it.author = author
        it.description = description
        it.genre = genre.joinToString(", ")
        it.status = when (status) {
            "ONGOING" -> SManga.ONGOING
            "COMPLETED" -> SManga.COMPLETED
            "LICENSED" -> SManga.LICENSED
            else -> SManga.UNKNOWN // covers "UNKNOWN" and other Impossible cases
        }
    }

    private fun ChapterDataClass.toSChapter() = SChapter.create().also {
        it.url = "$mangaId $index"
        it.name = name
        it.date_upload = uploadDate
        it.scanlator = scanlator
    }

    private val checkedBaseUrl: String
        get(): String = baseUrl.ifEmpty { throw RuntimeException("Set Tachidesk server url in extension settings") }

    private fun paginateResults(mangaList: List<MangaDataClass>, page: Int?, itemsPerPage: Int?): Pair<List<MangaDataClass>, Boolean> {
        var hasNextPage = false
        val pageItems = if (mangaList.isNotEmpty() && itemsPerPage is Int && page is Int) {
            val fromIndex = (page - 1) * itemsPerPage
            val toIndex = min(fromIndex + itemsPerPage, mangaList.size)
            hasNextPage = toIndex < mangaList.size
            mangaList.subList(fromIndex, toIndex)
        } else {
            mangaList
        }
        return Pair(pageItems, hasNextPage)
    }
}
