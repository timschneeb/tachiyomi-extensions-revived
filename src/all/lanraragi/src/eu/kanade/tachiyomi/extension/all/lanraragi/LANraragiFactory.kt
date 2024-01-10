package eu.kanade.tachiyomi.extension.all.lanraragi

import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceFactory

class LANraragiFactory : SourceFactory {
    override fun createSources(): List<Source> =
        listOf(
            LANraragi("1"),
            LANraragi("2"),
            LANraragi("3"),
        )
}
