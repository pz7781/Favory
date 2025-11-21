package project.favory.dto.comment.request

data class CreateCommentRequest(
    val favoryId: Long,
    val userId: Long,
    val content: String
)
