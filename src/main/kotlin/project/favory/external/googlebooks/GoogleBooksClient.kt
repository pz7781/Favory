package project.favory.external.googlebooks

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import project.favory.external.googlebooks.dto.GoogleBooksSearchResponse

@Component
class GoogleBooksClient(
    private val googleBooksRestClient: RestClient
) {

    fun searchBooks(query: String, maxResults: Int = 20, startIndex: Int = 0): GoogleBooksSearchResponse? {
        return googleBooksRestClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/volumes")
                    .queryParam("q", query)
                    .queryParam("maxResults", maxResults)
                    .queryParam("startIndex", startIndex)
                    .build()
            }
            .retrieve()
            .body(GoogleBooksSearchResponse::class.java)
    }
}

