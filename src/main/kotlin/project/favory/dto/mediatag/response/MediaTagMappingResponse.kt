package project.favory.dto.mediatag.response

import java.time.LocalDateTime

data class MediaTagMappingResponse(
    val id: Long,
    val mediaId: Long,
    val mediaTitle: String,
    val tagId: Long,
    val tagName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
