package project.favory.dto.comment.response

import java.time.LocalDateTime

data class CommentResponse(
    val id: Long,
    val favoryId: Long,
    val userId: Long,
    val userNickname: String,
    val userImageUrl: String? = null,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?
)
