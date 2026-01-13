package project.favory.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.common.PageResponse
import project.favory.dto.favory.response.TagInfo
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
        val category = request.category.trim().ifBlank { "all" }
        val sort = request.sort.trim().ifBlank { "latest" }

        val pageable: Pageable = PageRequest.of(request.page, request.size, toSort(sort))

        if (keyword.isBlank()) return emptyPage<Any>(pageable)

        if (userId != null) saveRecentSearch(userId, keyword)

        return when {
            keyword.startsWith("#") -> {
                val pureTag = keyword.removePrefix("#").trim()
                if (pureTag.isBlank()) {
                    emptyPage(pageable)
                } else {
                    searchByTag(pureTag, category, pageable)
                }
            }

            category.equals("profile", ignoreCase = true) -> {
                if (keyword.isBlank()) {
                    emptyPage(pageable)
                } else {
                    searchProfile(keyword, pageable)
                }
            }

            else -> searchMediaAndFavories(keyword, category, pageable)
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

    private fun searchByTag(tag: String, category: String, pageable: Pageable): PageResponse<SearchResultItem> {
        val mediaType: MediaType? =
            if (category.equals("all", ignoreCase = true)) null
            else runCatching { MediaType.valueOf(category.uppercase()) }.getOrNull()

        val idPage: Page<Long> =
            if (mediaType == null) {
                favoryTagMappingRepository.findFavoryIdsByTagPrefix(tag, pageable)
            } else {
                favoryTagMappingRepository.findFavoryIdsByTagPrefixAndMediaType(tag, mediaType, pageable)
            }

        val ids = idPage.content
        if (ids.isEmpty()) {
            return PageResponse(
                content = emptyList(),
                pageNumber = idPage.number,
                pageSize = idPage.size,
                totalElements = idPage.totalElements,
                totalPages = idPage.totalPages
            )
        }

        val favories = favoryRepository.findByIdInAndDeletedAtIsNull(ids)
        val favoryMap = favories.associateBy { it.id!! }
        val orderedFavories = ids.mapNotNull { favoryMap[it] }

        val tagsMap = buildTagsMap(orderedFavories)

        val content = orderedFavories.map { f -> f.toSearchResultItem(tagsMap[f.id!!] ?: emptyList()) }

        return PageResponse(
            content = content,
            pageNumber = idPage.number,
            pageSize = idPage.size,
            totalElements = idPage.totalElements,
            totalPages = idPage.totalPages
        )
    }

    private fun searchProfile(keyword: String, pageable: Pageable): PageResponse<SearchProfileItem> {
        val page = userRepository.findByNicknameContainingIgnoreCase(keyword, pageable)

        val content = page.content.map { user ->
            SearchProfileItem(
                id = user.id!!,
                nickname = user.nickname,
                profileImageUrl = user.profileImageUrl,
                profileMessage = user.profileMessage
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

    private fun searchMediaAndFavories(
        keyword: String,
        category: String,
        pageable: Pageable
    ): PageResponse<SearchResultItem> {

        val mediaType: MediaType? =
            if (category.equals("all", ignoreCase = true)) {
                null
            } else {
                runCatching { MediaType.valueOf(category.uppercase()) }.getOrNull()
            }

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

            val tagsMap = buildTagsMap(page.content)

            val content = page.content.map { f -> f.toSearchResultItem(tagsMap[f.id!!] ?: emptyList()) }

            return PageResponse(
                content = content,
                pageNumber = page.number,
                pageSize = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages
            )
        }

        if (tokens.size == 1) {
            val page = favoryRepository.searchCombined(tokens[0], mediaType, pageable)

            val tagsMap = buildTagsMap(page.content)

            val content = page.content.map { f -> f.toSearchResultItem(tagsMap[f.id!!] ?: emptyList()) }

            return PageResponse(
                content = content,
                pageNumber = page.number,
                pageSize = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages
            )
        }

        val basePage = favoryRepository.searchCombined(
            tokens[0],
            mediaType,
            Pageable.unpaged()
        )

        val candidates = basePage.content

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

        val tagsMap = buildTagsMap(pageContent)

        val content = pageContent.map { f -> f.toSearchResultItem(tagsMap[f.id!!] ?: emptyList()) }

        return PageResponse(
            content = content,
            pageNumber = pageNumber,
            pageSize = pageSize,
            totalElements = totalElements,
            totalPages = totalPages
        )
    }

    private fun buildTagsMap(favories: List<Favory>): Map<Long, List<TagInfo>> {
        val ids = favories.mapNotNull { it.id }
        if (ids.isEmpty()) return emptyMap()

        return favoryTagMappingRepository.findAllWithTagByFavoryIdIn(ids)
            .groupBy { it.favory.id!! }
            .mapValues { (_, mappings) ->
                mappings.map { m -> TagInfo(id = m.tag.id!!, name = m.tag.name) }
            }
    }

    private fun Favory.toSearchResultItem(tags: List<TagInfo>) = SearchResultItem(
        id = this.id!!,
        userId = this.user.id!!,
        userNickname = this.user.nickname,
        userImageUrl = this.user.profileImageUrl,
        mediaId = this.media.id!!,
        mediaTitle = this.media.title,
        mediaCreator = this.media.creator,
        mediaYear = this.media.year,
        mediaType = this.media.type.name,
        mediaImageUrl = this.media.imageUrl,
        title = this.title,
        content = this.content,
        tags = tags,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deletedAt = this.deletedAt
    )
}