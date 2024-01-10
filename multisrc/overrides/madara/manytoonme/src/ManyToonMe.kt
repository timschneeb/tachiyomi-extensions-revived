package eu.kanade.tachiyomi.revived.en.manytoonme

import eu.kanade.tachiyomi.multisrc.madara.Madara

class ManyToonMe : Madara("ManyToon.me", "https://manytoon.me", "en") {

    override val mangaSubString = "comic"

    override val useNewChapterEndpoint: Boolean = true
}
