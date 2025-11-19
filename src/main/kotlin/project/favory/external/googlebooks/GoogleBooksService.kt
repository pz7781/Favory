package project.favory.external.googlebooks

import org.springframework.stereotype.Service
import project.favory.external.googlebooks.dto.GoogleBooksSearchResponse

@Service
class GoogleBooksService(
    private val googleBooksClient: GoogleBooksClient
) {

    fun searchBooks(query: String, maxResults: Int = 20, startIndex: Int = 0): GoogleBooksSearchResponse? {
        return googleBooksClient.searchBooks(query, maxResults, startIndex)
    }
}
