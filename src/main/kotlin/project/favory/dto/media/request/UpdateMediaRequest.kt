package project.favory.dto.media.request

data class UpdateMediaRequest(
    val title: String,
    val year: Int,
    val imageUrl: String?
)
