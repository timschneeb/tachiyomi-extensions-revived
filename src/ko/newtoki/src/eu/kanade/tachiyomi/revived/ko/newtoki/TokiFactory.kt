package eu.kanade.tachiyomi.revived.ko.newtoki

import eu.kanade.tachiyomi.source.SourceFactory

class TokiFactory : SourceFactory {
    override fun createSources() = listOf(ManaToki, NewTokiWebtoon)
}
