package eu.kanade.tachiyomi.revived.all.mangadex.dto

import eu.kanade.tachiyomi.revived.all.mangadex.MDConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(MDConstants.user)
data class UserDto(override val attributes: UserAttributes? = null) : EntityDto()

@Serializable
data class UserAttributes(val username: String) : AttributesDto()
