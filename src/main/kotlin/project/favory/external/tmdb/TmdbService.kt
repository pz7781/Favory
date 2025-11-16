package project.favory.external.tmdb

import org.springframework.stereotype.Service
import project.favory.external.tmdb.dto.TmdbMovie
import project.favory.external.tmdb.dto.TmdbMovieDetail
import project.favory.external.tmdb.dto.TmdbTv
import project.favory.external.tmdb.dto.TmdbTvDetail

@Service
class TmdbService(
    private val tmdbClient: TmdbClient
) {

    fun searchMovies(query: String, page: Int = 1): List<TmdbMovie> {
        val response = tmdbClient.searchMovies(query, page = page)
        return response?.results ?: emptyList()
    }

    fun searchTvShows(query: String, page: Int = 1): List<TmdbTv> {
        val response = tmdbClient.searchTv(query, page = page)
        return response?.results ?: emptyList()
    }

    fun getMovieDetail(movieId: Long): TmdbMovieDetail? {
        return tmdbClient.getMovieDetail(movieId)
    }

    fun getTvDetail(tvId: Long): TmdbTvDetail? {
        return tmdbClient.getTvDetail(tvId)
    }

    fun getDirector(movieDetail: TmdbMovieDetail): String? {
        return movieDetail.credits?.crew
            ?.firstOrNull { it.job == "Director" }
            ?.name
    }

    fun getPosterUrl(path: String?): String? {
        return tmdbClient.getImageUrl(path, "w500")
    }
}
