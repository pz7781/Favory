package project.favory.dto.searchRecent.request

data class SearchRequest(
    val keyword: String,
    val category: String = "all", // all, music, movie, drama, book, profile
    val sort: String = "latest", // latest, oldest, popular
    val page: Int = 0,
    val size: Int = 10
) {
}