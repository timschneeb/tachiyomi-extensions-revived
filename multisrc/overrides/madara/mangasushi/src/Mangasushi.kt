package eu.kanade.tachiyomi.revived.en.mangasushi

import eu.kanade.tachiyomi.multisrc.madara.Madara

class Mangasushi : Madara("Mangasushi", "https://mangasushi.org", "en") {
    override val useNewChapterEndpoint: Boolean = true
}
