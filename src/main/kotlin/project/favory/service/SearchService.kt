package project.favory.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.common.PageResponse
import project.favory.dto.searchRecent.request.SearchRequest
import project.favory.dto.searchRecent.response.SearchProfileItem
import project.favory.dto.searchRecent.response.SearchResultItem
import project.favory.entity.Favory
import project.favory.entity.MediaType
import project.favory.entity.SearchRecent
import project.favory.repository.FavoryRepository
import project.favory.repository.FavoryTagMappingRepository
import project.favory.repository.SearchRecentRepository
import project.favory.repository.UserRepository
import java.time.LocalDateTime

@Service
class SearchService(
    private val searchRecentRepository: SearchRecentRepository,
    private val userRepository: UserRepository,
    private val favoryRepository: FavoryRepository,
    private val favoryTagMappingRepository: FavoryTagMappingRepository
) {
    fun search(userId: Long?, request: SearchRequest): PageResponse<*> {
        val keyword = request.keyword.trim()

        // 최근 검색어 저장
        if (userId != null && keyword.isNotBlank()) {
            saveRecentSearch(userId, keyword)
        }

        val pageable: Pageable = PageRequest.of(request.page, request.size, toSort(request.sort))

        return when {
            // #해시태그
            keyword.startsWith("#") -> {
                val pureTag = keyword.removePrefix("#")
                if (pureTag.isBlank()) {
                    emptyPage(pageable)
                } else {
                    searchByTag(pureTag, pageable)
                }
            }

            // 프로필
            request.category.equals("PROFILE", ignoreCase = true) -> {
                if (keyword.isBlank()) {
                    emptyPage(pageable)
                } else {
                    searchProfile(keyword, pageable)
                }
            }

            // 전체, 카테고리
            else -> searchMediaAndFavories(keyword, request.category, pageable)
        }
    }

    private fun <T> emptyPage(pageable: Pageable) = PageResponse<T>(
        content = emptyList(),
        pageNumber = pageable.pageNumber,
        pageSize = pageable.pageSize,
        totalElements = 0,
        totalPages = 0
    )

    private fun toSort(sort: String): Sort =
        when (sort.lowercase()) {
            "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt")
            else -> Sort.by(Sort.Direction.DESC, "createdAt")
        }

    // 최근 검색어 저장
    @Transactional
    fun saveRecentSearch(userId: Long, keyword: String) {
        val user = userRepository.findByIdOrNull(userId) ?: return

        val existing = searchRecentRepository.findByUserIdAndKeyword(userId, keyword)
        val now = LocalDateTime.now()

        if (existing != null) {
            existing.lastUsedAt = now
        } else {
            val searchRecent = SearchRecent(
                user = user,
                keyword = keyword,
                lastUsedAt = now
            )
            searchRecentRepository.save(searchRecent)
        }

        val all = searchRecentRepository.findByUserIdOrderByLastUsedAtDesc(userId)
        if(all.size > 5) {
            val toDelete = all.drop(5)
            searchRecentRepository.deleteAll(toDelete)
        }
    }

    @Transactional(readOnly = true)
    fun getRecentSearches(userId: Long): List<String> {
        val recents = searchRecentRepository.findTop5ByUserIdOrderByLastUsedAtDesc(userId)
        return recents.map { it.keyword }
    }

    @Transactional
    fun clearRecentSearches(userId: Long) {
        searchRecentRepository.deleteAllByUserId(userId)
    }

    // 해시태그
    private fun searchByTag(tag: String, pageable: Pageable): PageResponse<SearchResultItem> {
        val mappingPage = favoryTagMappingRepository.findByTagNameContainingIgnoreCase(tag, pageable)
        val favoryPage = mappingPage.map { it.favory }

        val content: List<SearchResultItem> = favoryPage.content
            .filter { it.deletedAt == null }
            .map { it.toSearchResultItem() }

        return PageResponse(
            content = content,
            pageNumber = favoryPage.number,
            pageSize = favoryPage.size,
            totalElements = favoryPage.totalElements,
            totalPages = favoryPage.totalPages
        )
    }

    // 프로필
    private fun searchProfile(keyword: String, pageable: Pageable): PageResponse<SearchProfileItem> {
        val page = userRepository.findByNicknameContainingIgnoreCase(keyword, pageable)

        val content = page.content.map { user ->
            SearchProfileItem(
                id = user.id!!,
                nickname = user.nickname,
                description = user.profileMessage,
                profileImageUrl = user.profileImageUrl
            )
        }

        return PageResponse(
            content = content,
            pageNumber = page.number,
            pageSize = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages
        )
    }

    // 전체
    private fun searchMediaAndFavories(
        keyword: String,
        category: String,
        pageable: Pageable
    ): PageResponse<SearchResultItem> {

        val mediaType: MediaType? =
            if (category.equals("ALL", ignoreCase = true)) {
                null
            } else {
                runCatching { MediaType.valueOf(category.uppercase()) }.getOrNull()
            }

        // 토큰 분리
        val tokens = keyword.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        if (tokens.isEmpty()) {
            val page =
                if (mediaType == null) {
                    favoryRepository.findByDeletedAtIsNull(pageable)
                } else {
                    favoryRepository.findByMedia_TypeAndDeletedAtIsNull(mediaType, pageable)
                }

            val content = page.content
                .filter { it.deletedAt == null }
                .map { it.toSearchResultItem() }

            return PageResponse(
                content = content,
                pageNumber = page.number,
                pageSize = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages
            )
        }

        // 토큰이 1개일 경우
        if (tokens.size == 1) {
            val page = favoryRepository.searchCombined(tokens[0], mediaType, pageable)

            val content = page.content
                .filter { it.deletedAt == null }
                .map { it.toSearchResultItem() }

            return PageResponse(
                content = content,
                pageNumber = page.number,
                pageSize = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages
            )
        }

        // 토큰이 2개 이상인 경우
        val basePage = favoryRepository.searchCombined(
            tokens[0],
            mediaType,
            Pageable.unpaged()
        )

        val candidates = basePage.content
            .filter { it.deletedAt == null }

        val filtered = candidates.filter { favory ->
            val media = favory.media

            tokens.all { token ->
                val t = token.lowercase()

                media.title.contains(t, ignoreCase = true) ||
                        (media.creator?.contains(t, ignoreCase = true) == true) ||
                        favory.title.contains(t, ignoreCase = true)
            }
        }

        val pageSize = pageable.pageSize
        val pageNumber = pageable.pageNumber

        val totalElements = filtered.size.toLong()
        val totalPages =
            if (totalElements == 0L) 0
            else ((totalElements + pageSize - 1) / pageSize).toInt()

        val fromIndex = pageNumber * pageSize
        val toIndex = minOf(fromIndex + pageSize, filtered.size)

        val pageContent = if (fromIndex >= filtered.size) {
            emptyList()
        } else {
            filtered.subList(fromIndex, toIndex)
        }

        val content = pageContent.map { it.toSearchResultItem() }

        return PageResponse(
            content = content,
            pageNumber = pageNumber,
            pageSize = pageSize,
            totalElements = totalElements,
            totalPages = totalPages
        )
    }

    private fun Favory.toSearchResultItem() = SearchResultItem(
        id = this.id!!,
        title = this.media.title,
        subtitle = this.media.creator ?: "",
        category = this.media.type.name,
        reviewTitle = this.title,
        thumbnailUrl = this.media.imageUrl,
        createdAt = this.createdAt,
        tags = favoryTagMappingRepository.findAllByFavoryId(this.id!!).map { it.tag.name }
    )
}