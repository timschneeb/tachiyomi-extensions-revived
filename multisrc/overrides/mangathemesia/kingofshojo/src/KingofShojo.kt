package eu.kanade.tachiyomi.revived.ar.kingofshojo

import eu.kanade.tachiyomi.multisrc.mangathemesia.MangaThemesia
import java.text.SimpleDateFormat
import java.util.Locale

class KingofShojo : MangaThemesia("King of Shojo", "https://kingofshojo.com", "ar", dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale("ar"))) {

    override val hasProjectPage = true
}
