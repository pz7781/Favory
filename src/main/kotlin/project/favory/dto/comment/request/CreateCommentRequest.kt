package project.favory.dto.comment.request

data class CreateCommentRequest(
    val reviewId: Long,
    val userId: Long,
    val content: String
)
