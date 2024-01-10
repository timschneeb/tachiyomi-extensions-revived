package eu.kanade.tachiyomi.extension.all.tachidesk

import kotlinx.serialization.Serializable

@Serializable
data class SourceDataClass(
    val id: String,
    val name: String?,
    val lang: String?,
    val iconUrl: String?,

    /** The Source provides a latest listing */
    val supportsLatest: Boolean?,

    /** The Source implements [ConfigurableSource] */
    val isConfigurable: Boolean?,

    /** The Source class has a @Nsfw annotation */
    val isNsfw: Boolean?,

    /** A nicer version of [name] */
    val displayName: String?,
)

@Serializable
data class MangaDataClass(
    val id: Int,
    val sourceId: String,

    val url: String,
    val title: String,
    val thumbnailUrl: String? = null,

    val initialized: Boolean = false,

    val artist: String? = null,
    val author: String? = null,
    val description: String? = null,
    val genre: List<String> = emptyList(),
    val status: String = "UNKNOWN",
    val inLibrary: Boolean = false,
    val inLibraryAt: Int = 0,
    val source: SourceDataClass? = null,
    val meta: Map<String, String> = emptyMap(),
    val chapterCount: Int? = 0,

    val realUrl: String? = null,

    val freshData: Boolean = false,
)

@Serializable
data class ChapterDataClass(
    val url: String,
    val name: String,
    val uploadDate: Long,
    val chapterNumber: Float,
    val scanlator: String?,
    val mangaId: Int,

    /** chapter is read */
    val read: Boolean,

    /** chapter is bookmarked */
    val bookmarked: Boolean,

    /** last read page, zero means not read/no data */
    val lastPageRead: Int,

    /** last read page, zero means not read/no data */
    val lastReadAt: Long,

    /** this chapter's index, starts with 1 */
    val index: Int,

    /** is chapter downloaded */
    val downloaded: Boolean,

    /** used to construct pages in the front-end */
    val pageCount: Int = -1,

    /** total chapter count, used to calculate if there's a next and prev chapter */
    val chapterCount: Int? = null,

    /** used to store client specific values */
    val meta: Map<String, String> = emptyMap(),
)

@Serializable
data class CategoryDataClass(
    val id: Int,
    val order: Int,
    val name: String,
    val default: Boolean,
)
