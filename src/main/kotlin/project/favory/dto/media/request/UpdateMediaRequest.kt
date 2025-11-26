package project.favory.dto.media.request

data class UpdateMediaRequest(
    val title: String,
    val creator: String? = null,
    val year: Int?,
    val imageUrl: String?
)
