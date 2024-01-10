package eu.kanade.tachiyomi.revived.all.ehentai

import android.net.Uri

/**
 * Uri filter
 */
interface UriFilter {
    fun addToUri(builder: Uri.Builder)
}
