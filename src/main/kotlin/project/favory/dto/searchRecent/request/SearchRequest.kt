package project.favory.dto.searchRecent.request

class SearchRequest(
    val keyword: String,
    val category: String = "ALL", // ALL, MUSIC, MOVIE, DRAMA, BOOK, PROFILE
    val sort: String = "LATEST", // LATEST, OLDEST
    val page: Int = 0,
    val size: Int = 10
) {
}