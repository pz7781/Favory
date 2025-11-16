package project.favory.external.googlebooks.dto

data class GoogleBooksSearchResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    val id: String,
    val volumeInfo: GoogleBookVolumeInfo
)

data class GoogleBookVolumeInfo(
    val title: String,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val pageCount: Int?,
    val categories: List<String>?,
    val imageLinks: GoogleBookImageLinks?,
    val language: String?
)

data class GoogleBookImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?
)
