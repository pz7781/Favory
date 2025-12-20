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
import project.favory.entity.FavoryTagMapping
import project.favory.entity.MediaType
import project.favory.entity.Tag
import project.favory.repository.*
import project.favory.common.exception.*
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class FavoryService(
    private val favoryRepository: FavoryRepository,
    private val userRepository: UserRepository,
    private val mediaRepository: MediaRepository,
    private val commentRepository: CommentRepository,
    private val favoryTagMappingRepository: FavoryTagMappingRepository,
    private val tagRepository: TagRepository,
    private val authService: AuthService
) {

    @Transactional
    fun createFavory(request: CreateFavoryRequest): FavoryResponse {
        val user = userRepository.findByIdOrNull(request.userId)
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)

        val media = mediaRepository.findByIdOrNull(request.mediaId)
            ?: throw NotFoundException(ErrorCode.MEDIA_NOT_FOUND)

        val favory = Favory(
            user = user,
            media = media,
            title = request.title,
            content = request.content,
            deletedAt = null
        )

        val savedFavory = favoryRepository.save(favory)

        request.tagNames.forEach { tagName ->
            val trimmedName = tagName.trim()
            if (trimmedName.isNotEmpty()) {
                val tag = tagRepository.findAll().find { it.name == trimmedName }
                    ?: tagRepository.save(Tag(name = trimmedName))

                val mapping = FavoryTagMapping(
                    favory = savedFavory,
                    tag = tag
                )
                favoryTagMappingRepository.save(mapping)
            }
        }

        return savedFavory.toResponse()
    }

    fun getFavory(id: Long): FavoryResponse {
        val favory = favoryRepository.findByIdOrNull(id)
            ?: throw NotFoundException(ErrorCode.FAVORY_NOT_FOUND)

        if (favory.deletedAt != null) {
            throw NotFoundException(ErrorCode.FAVORY_DELETED)
        }

        return favory.toResponse()
    }

    fun getAllFavories(
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "latest",
        type: MediaType? = null
    ): PageResponse<FavoryResponse> {
        val sort = when (sortBy) {
            "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt")
            else -> Sort.by(Sort.Direction.DESC, "createdAt")
        }

        val pageable: Pageable = PageRequest.of(page, size, sort)
        val favoryPage: Page<Favory> = if (type != null) {
            favoryRepository.findByMedia_TypeAndDeletedAtIsNull(type, pageable)
        } else {
            favoryRepository.findByDeletedAtIsNull(pageable)
        }

        val content = favoryPage.content.map { it.toResponse() }

        return PageResponse(
            content = content,
            pageNumber = favoryPage.number,
            pageSize = favoryPage.size,
            totalElements = favoryPage.totalElements,
            totalPages = favoryPage.totalPages
        )
    }

    fun getFavoriesByMedia(mediaId: Long): List<FavoryResponse> {
        mediaRepository.findByIdOrNull(mediaId)
            ?: throw NotFoundException(ErrorCode.MEDIA_NOT_FOUND)

        return favoryRepository.findAll()
            .filter { it.media.id == mediaId && it.deletedAt == null }
            .map { it.toResponse() }
    }

    @Transactional
    fun updateFavory(id: Long, request: UpdateFavoryRequest): FavoryResponse {
        val favory = favoryRepository.findByIdOrNull(id)
            ?: throw NotFoundException(ErrorCode.FAVORY_NOT_FOUND)

        if (favory.deletedAt != null) {
            throw NotFoundException(ErrorCode.FAVORY_DELETED)
        }

        authService.validateUser(favory.user.id!!)

        favory.title = request.title
        favory.content = request.content

        if (request.tagNames != null) {
            val existingMappings = favoryTagMappingRepository.findAllByFavoryId(id)
            existingMappings.forEach { favoryTagMappingRepository.delete(it) }

            if (request.tagNames.isNotEmpty()) {
                request.tagNames.forEach { tagName ->
                    val trimmedName = tagName.trim()
                    if (trimmedName.isNotEmpty()) {
                        val tag = tagRepository.findAll().find { it.name == trimmedName }
                            ?: tagRepository.save(Tag(name = trimmedName))

                        val mapping = FavoryTagMapping(
                            favory = favory,
                            tag = tag
                        )
                        favoryTagMappingRepository.save(mapping)
                    }
                }
            }
        }

        return favory.toResponse()
    }

    @Transactional
    fun deleteFavory(id: Long) {
        val favory = favoryRepository.findByIdOrNull(id)
            ?: throw NotFoundException(ErrorCode.FAVORY_NOT_FOUND)

        authService.validateUser(favory.user.id!!)

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
