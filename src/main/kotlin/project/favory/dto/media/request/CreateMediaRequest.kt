package project.favory.dto.media.request

import project.favory.entity.MediaType

data class CreateMediaRequest(
    val type: MediaType,
    val title: String,
    val credit: String,
    val year: Int,
    val imageUrl: String?,
    val tagIds: List<Long>? = null
)
