package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import project.favory.dto.common.PageResponse
import project.favory.dto.searchRecent.request.SearchRequest
import project.favory.entity.User
import project.favory.repository.UserRepository
import project.favory.service.SearchService

@Tag(name = "Search", description = "검색 결과 조회, 최근 검색어 조회/삭제")
@RestController
@RequestMapping("/search")
class SearchController (
    private val searchService: SearchService,
    private val userRepository: UserRepository
){

    @Operation(summary = "검색 결과 조회")
    @GetMapping
    fun search(
        request: SearchRequest
    ): PageResponse<*> {
        val userId = getCurrentUserOrNull()?.id
        return searchService.search(userId, request)
    }

    private fun getCurrentUserOrNull(): User? {
        val auth = SecurityContextHolder.getContext().authentication ?: return null
        if (!auth.isAuthenticated || auth.principal == "anonymousUser") {
            return null
        }

        val email = auth.name ?: return null
        return userRepository.findByEmail(email)
    }

    @Operation(summary = "최근 검색어 조회")
    @GetMapping("/recent")
    fun getRecents(): List<String> {
        val user = getCurrentUserOrNull() ?: return emptyList()
        return searchService.getRecentSearches(user.id!!)
    }

    @Operation(summary = "최근 검색어 전체 삭제")
    @DeleteMapping("/recent")
    fun clearAll() {
        val user = getCurrentUserOrNull() ?: return
        searchService.clearRecentSearches(user.id!!)
    }
}