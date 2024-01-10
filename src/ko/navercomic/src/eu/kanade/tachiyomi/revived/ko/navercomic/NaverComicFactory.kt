package eu.kanade.tachiyomi.revived.ko.navercomic

import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceFactory

class NaverComicFactory : SourceFactory {
    override fun createSources(): List<Source> = listOf(
        NaverWebtoon(),
        NaverBestChallenge(),
        NaverChallenge(),
    )
}
