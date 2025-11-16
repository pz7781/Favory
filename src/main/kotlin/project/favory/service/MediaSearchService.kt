package project.favory.service

import org.springframework.stereotype.Service
import project.favory.dto.media.response.MediaSearchResponse
import project.favory.dto.media.response.MediaSearchResult
import project.favory.entity.MediaType
import project.favory.external.googlebooks.GoogleBooksService
import project.favory.external.spotify.SpotifyService
import project.favory.external.tmdb.TmdbService

@Service
class MediaSearchService(
    private val spotifyService: SpotifyService,
    private val tmdbService: TmdbService,
    private val googleBooksService: GoogleBooksService
) {

    fun searchMedia(keyword: String, mediaType: MediaType, limit: Int = 10): MediaSearchResponse {
        val results = when (mediaType) {
            MediaType.MUSIC -> searchMusic(keyword, limit)
            MediaType.MOVIE -> searchMovies(keyword, limit)
            MediaType.DRAMA -> searchDramas(keyword, limit)
            MediaType.BOOK -> searchBooks(keyword, limit)
        }

        return MediaSearchResponse(results = results)
    }

    private fun searchMusic(keyword: String, limit: Int): List<MediaSearchResult> {
        val response = spotifyService.searchTracks(keyword, limit) ?: return emptyList()

        return response.tracks?.items?.map { track ->
            MediaSearchResult(
                title = track.name,
                creator = track.artists.firstOrNull()?.name ?: "",
                year = extractYearFromDate(track.album.releaseDate),
                imageUrl = track.album.images.firstOrNull()?.url,
                mediaType = MediaType.MUSIC,
                externalId = "spotify:${track.id}"
            )
        } ?: emptyList()
    }

    private fun searchMovies(keyword: String, limit: Int): List<MediaSearchResult> {
        val movies = tmdbService.searchMovies(keyword)
            .take(limit)

        return movies.mapNotNull { movie ->
            val detail = tmdbService.getMovieDetail(movie.id)
            val director = detail?.let { tmdbService.getDirector(it) } ?: ""
            val posterUrl = tmdbService.getPosterUrl(movie.posterPath)

            MediaSearchResult(
                title = movie.title,
                creator = director,
                year = extractYearFromDate(movie.releaseDate),
                imageUrl = posterUrl,
                mediaType = MediaType.MOVIE,
                externalId = "tmdb:movie:${movie.id}"
            )
        }
    }

    private fun searchDramas(keyword: String, limit: Int): List<MediaSearchResult> {
        val tvShows = tmdbService.searchTvShows(keyword)
            .take(limit)

        return tvShows.mapNotNull { tv ->
            val detail = tmdbService.getTvDetail(tv.id)
            val creator = detail?.credits?.crew
                ?.firstOrNull { it.job in listOf("Producer", "Executive Producer", "Creator") }
                ?.name ?: ""
            val posterUrl = tmdbService.getPosterUrl(tv.posterPath)

            MediaSearchResult(
                title = tv.name,
                creator = creator,
                year = extractYearFromDate(tv.firstAirDate),
                imageUrl = posterUrl,
                mediaType = MediaType.DRAMA,
                externalId = "tmdb:tv:${tv.id}"
            )
        }
    }

    private fun searchBooks(keyword: String, limit: Int): List<MediaSearchResult> {
        val response = googleBooksService.searchBooks(keyword, limit) ?: return emptyList()

        return response.items?.map { book ->
            MediaSearchResult(
                title = book.volumeInfo.title,
                creator = book.volumeInfo.authors?.firstOrNull() ?: "",
                year = extractYearFromDate(book.volumeInfo.publishedDate),
                imageUrl = book.volumeInfo.imageLinks?.thumbnail
                    ?: book.volumeInfo.imageLinks?.smallThumbnail,
                mediaType = MediaType.BOOK,
                externalId = "googlebooks:${book.id}"
            )
        } ?: emptyList()
    }

    private fun extractYearFromDate(dateString: String?): Int {
        if (dateString.isNullOrBlank()) return 0

        return try {
            dateString.substring(0, 4).toInt()
        } catch (e: Exception) {
            0
        }
    }
}
