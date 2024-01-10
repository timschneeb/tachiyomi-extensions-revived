package eu.kanade.tachiyomi.revived.en.bananamanga

import eu.kanade.tachiyomi.multisrc.madara.Madara

class BananaManga : Madara("Banana Manga", "https://bananamanga.net", "en") {
    override val useNewChapterEndpoint = true

    override fun searchPage(page: Int): String = if (page == 1) "" else "page/$page/"
}
