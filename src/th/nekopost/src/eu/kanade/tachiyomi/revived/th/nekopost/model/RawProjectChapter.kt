package eu.kanade.tachiyomi.revived.th.nekopost.model

import kotlinx.serialization.Serializable

@Serializable
data class RawProjectChapter(
    val chapterId: String?,
    val chapterNo: String,
    val chapterName: String,
    val status: String,
    val publishDate: String,
    val createDate: String,
    val providerName: String,
)
