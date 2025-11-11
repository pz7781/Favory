package project.favory.dto.review.request

data class CreateReviewRequest(
    val userId: Long,
    val mediaId: Long,
    val title: String,
    val content: String
)
