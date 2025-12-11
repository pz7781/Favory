package project.favory.dto.searchRecent.response

// 프로필 탭
class SearchProfileItem(
    val id: Long,
    val nickname: String,
    val description: String?,
    val profileImageUrl: String?
) {
}