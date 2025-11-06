package project.favory.dto.user.response

class UserResponse (
    val id: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val profileMessage: String?
){
}