package project.favory.dto.media.response

import project.favory.entity.MediaType
import java.time.LocalDateTime

data class MediaResponse(
    val id: Long,
    val externalId: String,
    val mediaType: MediaType,
    val title: String,
    val creator: String? = null,
    val year: Int?,
    val imageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
