package project.favory.external.spotify

import org.springframework.stereotype.Service
import project.favory.external.spotify.dto.SpotifySearchResponse

@Service
class SpotifyService(
    private val spotifyClient: SpotifyClient
) {

    fun searchTracks(query: String, limit: Int = 20): SpotifySearchResponse? {
        return spotifyClient.search(query, "track", limit)
    }
}
