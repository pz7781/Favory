package project.favory.dto.searchRecent.response

import project.favory.dto.favory.response.TagInfo
import java.time.LocalDateTime

class SearchResultItem(
    val id: Long,
    val userId: Long,
    val userNickname: String,
    val userImageUrl: String?,
    val mediaTitle: String,
    val mediaCreator: String?, // 가수, 감독, 방송사, 작가
    val mediaYear: Int?,
    val mediaType: String, // MUSIC, MOVIE, DRAMA, BOOK
    val mediaImageUrl: String?,
    val title: String?,
    val content: String,
    val tags: List<TagInfo>,
    val createdAt: LocalDateTime
) {
}