package project.favory.external.tmdb.dto

import com.fasterxml.jackson.annotation.JsonProperty

// 영화 검색 응답
data class TmdbMovieSearchResponse(
    val page: Int,
    val results: List<TmdbMovie>,
    @JsonProperty("total_pages")
    val totalPages: Int,
    @JsonProperty("total_results")
    val totalResults: Int
)

// 영화 정보
data class TmdbMovie(
    val id: Long,
    val title: String,
    @JsonProperty("original_title")
    val originalTitle: String,
    val overview: String?,
    @JsonProperty("poster_path")
    val posterPath: String?,
    @JsonProperty("backdrop_path")
    val backdropPath: String?,
    @JsonProperty("release_date")
    val releaseDate: String?,
    @JsonProperty("vote_average")
    val voteAverage: Double,
    val adult: Boolean,
    @JsonProperty("genre_ids")
    val genreIds: List<Int>
)

// TV 검색 응답
data class TmdbTvSearchResponse(
    val page: Int,
    val results: List<TmdbTv>,
    @JsonProperty("total_pages")
    val totalPages: Int,
    @JsonProperty("total_results")
    val totalResults: Int
)

// TV 정보
data class TmdbTv(
    val id: Long,
    val name: String,
    @JsonProperty("original_name")
    val originalName: String,
    val overview: String?,
    @JsonProperty("poster_path")
    val posterPath: String?,
    @JsonProperty("backdrop_path")
    val backdropPath: String?,
    @JsonProperty("first_air_date")
    val firstAirDate: String?,
    @JsonProperty("vote_average")
    val voteAverage: Double,
    @JsonProperty("genre_ids")
    val genreIds: List<Int>
)

// 영화 상세 정보
data class TmdbMovieDetail(
    val id: Long,
    val title: String,
    @JsonProperty("original_title")
    val originalTitle: String,
    val overview: String?,
    @JsonProperty("poster_path")
    val posterPath: String?,
    @JsonProperty("backdrop_path")
    val backdropPath: String?,
    @JsonProperty("release_date")
    val releaseDate: String?,
    val runtime: Int?,
    @JsonProperty("vote_average")
    val voteAverage: Double,
    val genres: List<TmdbGenre>,
    val credits: TmdbCredits?
)

// TV 상세 정보
data class TmdbTvDetail(
    val id: Long,
    val name: String,
    @JsonProperty("original_name")
    val originalName: String,
    val overview: String?,
    @JsonProperty("poster_path")
    val posterPath: String?,
    @JsonProperty("backdrop_path")
    val backdropPath: String?,
    @JsonProperty("first_air_date")
    val firstAirDate: String?,
    @JsonProperty("vote_average")
    val voteAverage: Double,
    val genres: List<TmdbGenre>,
    @JsonProperty("number_of_seasons")
    val numberOfSeasons: Int,
    val credits: TmdbCredits?,
    val networks: List<TmdbNetwork>?
)

data class TmdbGenre(
    val id: Int,
    val name: String
)

data class TmdbCredits(
    val cast: List<TmdbCast>,
    val crew: List<TmdbCrew>
)

data class TmdbCast(
    val id: Long,
    val name: String,
    val character: String,
    @JsonProperty("profile_path")
    val profilePath: String?
)

data class TmdbCrew(
    val id: Long,
    val name: String,
    val job: String,
    val department: String
)

data class TmdbNetwork(
    val id: Long,
    val name: String,
    @JsonProperty("logo_path")
    val logoPath: String?,
    @JsonProperty("origin_country")
    val originCountry: String
)
