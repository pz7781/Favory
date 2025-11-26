package project.favory.dto.media.request

import project.favory.entity.MediaType

data class CreateMediaRequest(
    val externalId: String,
    val type: MediaType,
    val title: String,
    val creator: String? = null,
    val year: Int?,
    val imageUrl: String?
)
