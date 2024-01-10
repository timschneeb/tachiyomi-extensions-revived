package eu.kanade.tachiyomi.extension.id.shinigami

import eu.kanade.tachiyomi.multisrc.madara.Madara
import eu.kanade.tachiyomi.network.interceptor.rateLimit
import eu.kanade.tachiyomi.source.model.SChapter
import okhttp3.Headers
import okhttp3.OkHttpClient
import org.jsoup.nodes.Element
import java.util.concurrent.TimeUnit

class Shinigami : Madara("Shinigami", "https://shinigami.moe", "id") {
    // moved from Reaper Scans (id) to Shinigami (id)
    override val id = 3411809758861089969

    override val useNewChapterEndpoint = false

    override fun searchPage(page: Int): String = if (page == 1) "" else "page/$page/"

    override val client: OkHttpClient = super.client.newBuilder()
        .rateLimit(4, 1, TimeUnit.SECONDS)
        .build()

    override fun headersBuilder(): Headers.Builder = super.headersBuilder()
        .add("Sec-Fetch-Dest", "document")
        .add("Sec-Fetch-Mode", "navigate")
        .add("Sec-Fetch-Site", "same-origin")
        .add("Upgrade-Insecure-Requests", "1")

    override val mangaSubString = "semua-series"

    // Tags are useless as they are just SEO keywords.
    override val mangaDetailsSelectorTag = ""

    override fun chapterFromElement(element: Element): SChapter = SChapter.create().apply {
        val urlElement = element.selectFirst(chapterUrlSelector)!!

        name = urlElement.selectFirst("p.chapter-manhwa-title")?.text()
            ?: urlElement.ownText()
        date_upload = urlElement.selectFirst("span.chapter-release-date > i")?.text()
            .let { parseChapterDate(it) }

        val fixedUrl = urlElement.attr("abs:href")

        setUrlWithoutDomain(fixedUrl)
    }
}
