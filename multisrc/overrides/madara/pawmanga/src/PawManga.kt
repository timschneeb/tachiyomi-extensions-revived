package eu.kanade.tachiyomi.revived.en.pawmanga

import eu.kanade.tachiyomi.multisrc.madara.Madara

class PawManga : Madara("Paw Manga", "https://pawmanga.com", "en") {
    override val useNewChapterEndpoint = true

    override fun searchPage(page: Int): String = if (page == 1) "" else "page/$page/"
}
