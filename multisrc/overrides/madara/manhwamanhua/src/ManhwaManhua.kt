package eu.kanade.tachiyomi.revived.en.manhwamanhua

import eu.kanade.tachiyomi.multisrc.madara.Madara

class ManhwaManhua : Madara("ManhwaManhua", "https://manhwamanhua.com", "en") {
    override val useNewChapterEndpoint = true
    override val filterNonMangaItems = false

    override fun searchPage(page: Int): String = if (page == 1) "" else "page/$page/"
}
