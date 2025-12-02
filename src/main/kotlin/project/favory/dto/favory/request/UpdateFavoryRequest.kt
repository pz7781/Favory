package project.favory.dto.favory.request

data class UpdateFavoryRequest(
    val title: String,
    val content: String,
    val tagNames: List<String>? = null
)
