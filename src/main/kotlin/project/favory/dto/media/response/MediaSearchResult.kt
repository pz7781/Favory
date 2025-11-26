package project.favory.dto.media.response

import project.favory.entity.MediaType

data class MediaSearchResult(
    val title: String,
    val creator: String? = null,
    val year: Int?,
    val imageUrl: String?,
    val mediaType: MediaType,
    val externalId: String
)
