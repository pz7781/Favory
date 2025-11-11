package project.favory.dto.media.response

import project.favory.entity.MediaType
import java.time.LocalDateTime

data class MediaResponse(
    val id: Long,
    val type: MediaType,
    val title: String,
    val credit: String,
    val year: Int,
    val imageUrl: String?,
    val tags: List<TagInfo> = emptyList(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class TagInfo(
    val id: Long,
    val name: String
)
