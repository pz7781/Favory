package project.favory.external.spotify

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import project.favory.external.spotify.dto.SpotifySearchResponse
import project.favory.external.spotify.dto.SpotifyTokenResponse
import java.time.Instant
import java.util.Base64

@Component
class SpotifyClient(
    private val spotifyRestClient: RestClient,
    @Value("\${spotify.api.token-url}") private val tokenUrl: String,
    @Value("\${spotify.api.client-id}") private val clientId: String,
    @Value("\${spotify.api.client-secret}") private val clientSecret: String
) {

    private var accessToken: String? = null
    private var tokenExpiresAt: Instant? = null

    private fun getAccessToken(): String {
        if (accessToken != null && tokenExpiresAt != null && Instant.now().isBefore(tokenExpiresAt)) {
            return accessToken!!
        }

        val credentials = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())
        
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("grant_type", "client_credentials")

        val tokenClient = RestClient.create()
        val response = tokenClient.post()
            .uri(tokenUrl)
            .header("Authorization", "Basic $credentials")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(formData)
            .retrieve()
            .body(SpotifyTokenResponse::class.java)

        accessToken = response?.accessToken
        tokenExpiresAt = Instant.now().plusSeconds(response?.expiresIn?.toLong() ?: 3600)

        return accessToken!!
    }

    fun search(query: String, type: String = "track", limit: Int = 20): SpotifySearchResponse? {
        val token = getAccessToken()

        return spotifyRestClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/search")
                    .queryParam("q", query)
                    .queryParam("type", type)
                    .queryParam("limit", limit)
                    .queryParam("market", "KR")
                    .build()
            }
            .header("Authorization", "Bearer $token")
            .retrieve()
            .body(SpotifySearchResponse::class.java)
    }
}
