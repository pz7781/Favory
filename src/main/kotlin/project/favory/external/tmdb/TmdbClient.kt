package project.favory.external.tmdb

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import project.favory.external.tmdb.dto.*

@Component
class TmdbClient(
    private val tmdbRestClient: RestClient
) {

    fun searchMovies(query: String, language: String = "ko-KR", page: Int = 1): TmdbMovieSearchResponse? {
        return tmdbRestClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/search/movie")
                    .queryParam("query", query)
                    .queryParam("language", language)
                    .queryParam("page", page)
                    .build()
            }
            .retrieve()
            .body(TmdbMovieSearchResponse::class.java)
    }

    fun searchTv(query: String, language: String = "ko-KR", page: Int = 1): TmdbTvSearchResponse? {
        return tmdbRestClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/search/tv")
                    .queryParam("query", query)
                    .queryParam("language", language)
                    .queryParam("page", page)
                    .build()
            }
            .retrieve()
            .body(TmdbTvSearchResponse::class.java)
    }

    fun getMovieDetail(movieId: Long, language: String = "ko-KR"): TmdbMovieDetail? {
        return tmdbRestClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/movie/{movieId}")
                    .queryParam("language", language)
                    .queryParam("append_to_response", "credits")
                    .build(movieId)
            }
            .retrieve()
            .body(TmdbMovieDetail::class.java)
    }

    fun getTvDetail(tvId: Long, language: String = "ko-KR"): TmdbTvDetail? {
        return tmdbRestClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/tv/{tvId}")
                    .queryParam("language", language)
                    .queryParam("append_to_response", "credits")
                    .build(tvId)
            }
            .retrieve()
            .body(TmdbTvDetail::class.java)
    }

    fun getImageUrl(path: String?, size: String = "w500"): String? {
        return path?.let { "https://image.tmdb.org/t/p/$size$it" }
    }
}
