package project.favory.dto.favory.response

import project.favory.entity.MediaType
import java.time.LocalDateTime

data class FavoryResponse(
    val id: Long,
    val userId: Long,
    val userNickname: String,
    val userImageUrl: String? = null,
    val mediaId: Long,
    val mediaTitle: String,
    val mediaCreator: String? = null,
    val mediaYear: Int?,
    val mediaType: MediaType,
    val mediaImageUrl: String? = null,
    val title: String,
    val content: String,
    val tags: List<TagInfo> = emptyList(),
    val likeCount: Long,
    val likedByMe: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?
)

data class TagInfo(
    val id: Long,
    val name: String
)
