package project.favory.dto.favorytag.response

import java.time.LocalDateTime

data class FavoryTagMappingResponse(
    val id: Long,
    val favoryId: Long,
    val favoryTitle: String,
    val tagId: Long,
    val tagName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
