package eu.kanade.tachiyomi.revived.id.noromax

import eu.kanade.tachiyomi.multisrc.mangathemesia.MangaThemesia

class Noromax : MangaThemesia("Noromax", "https://noromax.my.id", "id", "/Komik") {

    // Site changed from ZeistManga to MangaThemesia
    override val versionId = 2

    override val hasProjectPage = true
}
