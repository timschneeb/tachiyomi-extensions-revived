package eu.kanade.tachiyomi.revived.all.holonometria

import eu.kanade.tachiyomi.source.SourceFactory

class HolonometriaFactory : SourceFactory {
    override fun createSources() = listOf(
        Holonometria("ja", ""),
        Holonometria("en"),
        Holonometria("id"),
    )
}
