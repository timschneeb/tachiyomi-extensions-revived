package eu.kanade.tachiyomi.revived.en.mangadistrict

import eu.kanade.tachiyomi.multisrc.madara.Madara

class MangaDistrict : Madara(
    "Manga District",
    "https://mangadistrict.com",
    "en",
) {
    override fun searchMangaNextPageSelector() = "div[role=navigation] a.last"
}
