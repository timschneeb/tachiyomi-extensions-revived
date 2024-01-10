package eu.kanade.tachiyomi.revived.en.webtoonstop

import eu.kanade.tachiyomi.multisrc.madara.Madara
import java.text.SimpleDateFormat
import java.util.Locale

class WebtoonsTOP : Madara(
    "WebtoonsTOP",
    "https://webtoons.top",
    "en",
    dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US),
)
