package project.favory.dto.tag.response

import java.time.LocalDateTime

data class TagResponse(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
