package eu.kanade.tachiyomi.revived.en.vizshonenjump

import eu.kanade.tachiyomi.source.SourceFactory

class VizFactory : SourceFactory {
    override fun createSources() = listOf(
        Viz("VIZ Shonen Jump", "shonenjump"),
        Viz("VIZ Manga", "vizmanga"),
    )
}
