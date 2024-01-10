package eu.kanade.tachiyomi.revived.es.raikiscan

import eu.kanade.tachiyomi.multisrc.mangathemesia.MangaThemesia
import java.text.SimpleDateFormat
import java.util.Locale

class RaikiScan : MangaThemesia("Raiki Scan", "https://raikiscan.com", "es", dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale("es")))
