package project.favory.dto.auth.response

import project.favory.dto.user.response.UserResponse

class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
) {
}