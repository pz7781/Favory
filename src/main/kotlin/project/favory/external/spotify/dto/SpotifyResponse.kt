package project.favory.external.spotify.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotifyTokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("expires_in") val expiresIn: Int
)

data class SpotifySearchResponse(
    val tracks: SpotifyTracks?
)

data class SpotifyTracks(
    val items: List<SpotifyTrack>,
    val total: Int
)

data class SpotifyTrack(
    val id: String,
    val name: String,
    val artists: List<SpotifyArtist>,
    val album: SpotifyAlbum,
    @JsonProperty("duration_ms") val durationMs: Int,
    @JsonProperty("external_urls") val externalUrls: SpotifyExternalUrls?
)

data class SpotifyArtist(
    val id: String,
    val name: String
)

data class SpotifyAlbum(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>,
    @JsonProperty("release_date") val releaseDate: String?
)

data class SpotifyImage(
    val url: String,
    val height: Int?,
    val width: Int?
)

data class SpotifyExternalUrls(
    val spotify: String?
)
