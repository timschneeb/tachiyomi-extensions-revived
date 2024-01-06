package eu.kanade.tachiyomi.revived.all.mangadex.dto

import eu.kanade.tachiyomi.revived.all.mangadex.MDConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(MDConstants.scanlationGroup)
data class ScanlationGroupDto(override val attributes: ScanlationGroupAttributes? = null) : EntityDto()

@Serializable
data class ScanlationGroupAttributes(val name: String) : AttributesDto()
