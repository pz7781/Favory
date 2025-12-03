package project.favory.dto.searchRecent.response

import java.time.LocalDateTime

// 공통
class SearchResultItem(
    val id: Long,
    val title: String,
    val subtitle: String?, // 가수, 감독, 방송사, 작가
    val category: String, // MUSIC, MOVIE, DRAMA, BOOK
    val reviewTitle: String?,
    val thumbnailUrl: String?,
    val createdAt: LocalDateTime,
    val tags: List<String>
) {
}