package project.favory.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.common.PageResponse
import project.favory.dto.favory.request.CreateFavoryRequest
import project.favory.dto.favory.request.UpdateFavoryRequest
import project.favory.dto.favory.response.FavoryResponse
import project.favory.dto.favory.response.TagInfo
import project.favory.entity.Favory
import project.favory.repository.*
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class FavoryService(
    private val favoryRepository: FavoryRepository,
    private val userRepository: UserRepository,
    private val mediaRepository: MediaRepository,
    private val commentRepository: CommentRepository,
    private val favoryTagMappingRepository: FavoryTagMappingRepository
) {

    @Transactional
    fun createFavory(request: CreateFavoryRequest): FavoryResponse {
        val user = userRepository.findByIdOrNull(request.userId)
            ?: throw IllegalArgumentException("User not found with id: ${request.userId}")

        val media = mediaRepository.findByIdOrNull(request.mediaId)
            ?: throw IllegalArgumentException("Media not found with id: ${request.mediaId}")

        val favory = Favory(
            user = user,
            media = media,
            title = request.title,
            content = request.content,
            deletedAt = null
        )

        val savedFavory = favoryRepository.save(favory)
        return savedFavory.toResponse()
    }

    fun getFavory(id: Long): FavoryResponse {
        val favory = favoryRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Favory not found with id: $id")

        if (favory.deletedAt != null) {
            throw IllegalArgumentException("Favory is deleted")
        }

        return favory.toResponse()
    }

    fun getAllFavories(page: Int = 0, size: Int = 10, sortBy: String = "latest"): PageResponse<FavoryResponse> {
        val sort = when (sortBy) {
            "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt")
            else -> Sort.by(Sort.Direction.DESC, "createdAt")
        }

        val pageable: Pageable = PageRequest.of(page, size, sort)
        val favoryPage: Page<Favory> = favoryRepository.findAll(pageable)

        val filteredContent = favoryPage.content
            .filter { it.deletedAt == null }
            .map { it.toResponse() }

        return PageResponse(
            content = filteredContent,
            pageNumber = favoryPage.number,
            pageSize = favoryPage.size,
            totalElements = favoryPage.totalElements,
            totalPages = favoryPage.totalPages
        )
    }

    fun getFavoriesByMedia(mediaId: Long): List<FavoryResponse> {
        mediaRepository.findByIdOrNull(mediaId)
            ?: throw IllegalArgumentException("Media not found with id: $mediaId")

        return favoryRepository.findAll()
            .filter { it.media.id == mediaId && it.deletedAt == null }
            .map { it.toResponse() }
    }

    @Transactional
    fun updateFavory(id: Long, request: UpdateFavoryRequest): FavoryResponse {
        val favory = favoryRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Favory not found with id: $id")

        if (favory.deletedAt != null) {
            throw IllegalArgumentException("Favory is deleted")
        }

        favory.title = request.title
        favory.content = request.content

        return favory.toResponse()
    }

    @Transactional
    fun deleteFavory(id: Long) {
        val favory = favoryRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Favory not found with id: $id")

        favory.deletedAt = LocalDateTime.now()

        val comments = commentRepository.findAllByFavoryId(id)
            .filter { it.deletedAt == null }

        comments.forEach { comment ->
            comment.deletedAt = LocalDateTime.now()
        }
    }

    private fun Favory.toResponse(): FavoryResponse {
        val tags = favoryTagMappingRepository.findAllByFavoryId(id!!)
            .map { TagInfo(id = it.tag.id!!, name = it.tag.name) }

        return FavoryResponse(
            id = id!!,
            userId = user.id!!,
            userNickname = user.nickname,
            userImageUrl = user.profileImageUrl,
            mediaId = media.id!!,
            mediaTitle = media.title,
            mediaCreator = media.creator,
            mediaYear = media.year,
            mediaType = media.type,
            mediaImageUrl = media.imageUrl,
            title = title,
            content = content,
            tags = tags,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }
}
