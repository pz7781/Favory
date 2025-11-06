package project.favory.dto.auth.response

class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String= "Bearer"
) {
}