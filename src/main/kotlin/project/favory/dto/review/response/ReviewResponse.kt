package project.favory.dto.review.response

import java.time.LocalDateTime

data class ReviewResponse(
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
