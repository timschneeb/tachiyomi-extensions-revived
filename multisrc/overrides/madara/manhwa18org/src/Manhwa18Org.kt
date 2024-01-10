package eu.kanade.tachiyomi.revived.en.manhwa18org

import eu.kanade.tachiyomi.multisrc.madara.Madara

class Manhwa18Org : Madara("Manhwa18.org", "https://manhwa18.org", "en") {

    // The website does not flag the content.
    override val filterNonMangaItems = false
}
