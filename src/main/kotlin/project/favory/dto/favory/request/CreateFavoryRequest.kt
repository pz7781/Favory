package project.favory.dto.favory.request

data class CreateFavoryRequest(
    val userId: Long,
    val mediaId: Long,
    val title: String,
    val content: String,
    val tagNames: List<String> = emptyList()
)
