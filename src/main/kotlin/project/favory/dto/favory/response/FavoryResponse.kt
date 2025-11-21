package project.favory.dto.favory.response

import java.time.LocalDateTime

data class FavoryResponse(
    val id: Long,
    val userId: Long,
    val userNickname: String,
    val mediaId: Long,
    val mediaTitle: String,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?
)
