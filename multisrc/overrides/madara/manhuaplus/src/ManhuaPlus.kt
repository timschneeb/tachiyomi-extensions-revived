package eu.kanade.tachiyomi.revived.en.manhuaplus

import eu.kanade.tachiyomi.multisrc.madara.Madara

class ManhuaPlus : Madara("Manhua Plus", "https://manhuaplus.com", "en") {

    // The website does not flag the content.
    override val filterNonMangaItems = false

    override val pageListParseSelector = ".read-container img"
}
