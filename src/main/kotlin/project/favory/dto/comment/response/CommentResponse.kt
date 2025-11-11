package project.favory.dto.comment.response

import java.time.LocalDateTime

data class CommentResponse(
    val id: Long,
    val reviewId: Long,
    val userId: Long,
    val userNickname: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?
)
