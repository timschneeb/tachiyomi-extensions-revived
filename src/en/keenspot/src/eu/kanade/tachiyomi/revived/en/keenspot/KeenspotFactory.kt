package eu.kanade.tachiyomi.revived.en.keenspot

import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceFactory

class KeenspotFactory : SourceFactory {
    override fun createSources(): List<Source> = listOf(TwoKinds())
}
