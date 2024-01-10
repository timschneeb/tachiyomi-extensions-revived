package eu.kanade.tachiyomi.revived.th.nekopost.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RawProjectCategory(
    @SerialName("cateName")
    val categoryName: String,
    @SerialName("cateLink")
    val categoryLink: String,
)
