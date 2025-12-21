package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.context.SecurityContextHolder
import project.favory.config.swagger.SecurityNotRequired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import project.favory.dto.common.PageResponse
import project.favory.dto.searchRecent.request.SearchRequest
import project.favory.entity.User
import project.favory.repository.UserRepository
import project.favory.service.SearchService
import project.favory.service.UserService

@Tag(name = "Search", description = "검색 결과 조회, 최근 검색어 조회/삭제")
@RestController
@RequestMapping("/search")
class SearchController (
    private val searchService: SearchService,
    private val userService: UserService
){

    @SecurityNotRequired
    @Operation(summary = "검색 결과 조회")
    @GetMapping
    fun search(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "all") category: String,
        @RequestParam(defaultValue = "latest") sort: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): PageResponse<*> {
        val request = SearchRequest(keyword, category, sort, page, size)
        val userId = userService.getCurrentUserOrNull()?.id
        return searchService.search(userId, request)
    }

    @Operation(summary = "최근 검색어 조회")
    @GetMapping("/recent")
    fun getRecents(): List<String> {
        val user = userService.getCurrentUserOrNull() ?: return emptyList()
        return searchService.getRecentSearches(user.id!!)
    }

    @Operation(summary = "최근 검색어 전체 삭제")
    @DeleteMapping("/recent")
    fun clearAll() {
        val user = userService.getCurrentUserOrNull() ?: return
        searchService.clearRecentSearches(user.id!!)
    }
}