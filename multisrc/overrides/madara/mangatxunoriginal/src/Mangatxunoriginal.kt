package eu.kanade.tachiyomi.revived.en.mangatxunoriginal

import eu.kanade.tachiyomi.multisrc.madara.Madara
import java.text.SimpleDateFormat
import java.util.Locale

class Mangatxunoriginal : Madara(
    "Manga-TX",
    "https://manga-tx.com",
    "en",
    dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US),
)
