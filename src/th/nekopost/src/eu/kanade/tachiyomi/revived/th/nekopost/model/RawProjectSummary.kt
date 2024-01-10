package eu.kanade.tachiyomi.revived.th.nekopost.model

import kotlinx.serialization.Serializable

@Serializable
data class RawProjectSummary(
    val cover: String,
    val chapterId: String,
    val chapterName: String,
    val chapterNo: String,
    val createDate: String,
    val providerName: String,
    val noNewChapter: String,
    val projectName: String,
    val projectId: String,
)
