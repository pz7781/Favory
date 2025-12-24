package project.favory.security.oauth

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import project.favory.common.exception.ErrorCode
import project.favory.common.exception.UnauthorizedException


data class GoogleUserInfo(
    val email: String,
    val name: String?,
    val picture: String?,
    val sub: String
)

@Component
class GoogleTokenVerifier(
    @Value("\${oauth.google.client-id}")
    private val googleClientId: String
) {

    private val verifier: GoogleIdTokenVerifier =
        GoogleIdTokenVerifier.Builder(
            NetHttpTransport(),
            JacksonFactory.getDefaultInstance()
        )
            .setAudience(listOf(googleClientId))
            .build()

    fun verify(idTokenString: String): GoogleUserInfo {
        val idToken: GoogleIdToken = verifier.verify(idTokenString)
            ?: throw UnauthorizedException(ErrorCode.INVALID_OAUTH_TOKEN)

        val payload = idToken.payload

        val email = payload.email
            ?: throw UnauthorizedException(ErrorCode.INVALID_OAUTH_TOKEN)

        val sub = payload.subject
            ?: throw UnauthorizedException(ErrorCode.INVALID_OAUTH_TOKEN)

        return GoogleUserInfo(
            email = email,
            name = payload["name"] as? String,
            picture = payload["picture"] as? String,
            sub = sub
        )
    }
}