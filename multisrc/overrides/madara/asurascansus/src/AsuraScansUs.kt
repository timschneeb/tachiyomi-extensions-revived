package eu.kanade.tachiyomi.revived.en.asurascansus

import eu.kanade.tachiyomi.multisrc.madara.Madara

class AsuraScansUs : Madara("Asura Scans.us (unoriginal)", "https://asurascans.com.tr", "en") {
    override val useNewChapterEndpoint = true

    override fun searchPage(page: Int): String = if (page == 1) "" else "page/$page/"
}
