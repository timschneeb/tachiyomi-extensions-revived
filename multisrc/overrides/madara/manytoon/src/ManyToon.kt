package eu.kanade.tachiyomi.revived.en.manytoon

import eu.kanade.tachiyomi.multisrc.madara.Madara

class ManyToon : Madara("ManyToon", "https://manytoon.com", "en") {

    override val mangaSubString = "comic"

    override val useNewChapterEndpoint: Boolean = true
}
