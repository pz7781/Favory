package project.favory.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.media.request.CreateMediaRequest
import project.favory.dto.media.request.UpdateMediaRequest
import project.favory.dto.media.response.MediaExistsResponse
import project.favory.dto.media.response.MediaResponse
import project.favory.dto.media.response.TagInfo
import project.favory.entity.Media
import project.favory.entity.MediaType
import project.favory.repository.MediaRepository
import project.favory.repository.MediaTagMappingRepository
import project.favory.repository.TagRepository

@Service
@Transactional(readOnly = true)
class MediaService(
    private val mediaRepository: MediaRepository,
    private val tagRepository: TagRepository,
    private val mediaTagMappingRepository: MediaTagMappingRepository
) {

    fun checkMediaExists(externalId: String): MediaExistsResponse {
        val media = mediaRepository.findByExternalId(externalId)
        return MediaExistsResponse(
            mediaId = media?.id
        )
    }

    @Transactional
    fun createMedia(request: CreateMediaRequest): MediaResponse {
        val media = Media(
            externalId = request.externalId,
            type = request.type,
            title = request.title,
            year = request.year,
            imageUrl = request.imageUrl
        )

        val savedMedia = mediaRepository.save(media)

        return savedMedia.toResponse()
    }

    fun getMedia(id: Long): MediaResponse {
        val media = mediaRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Media not found with id: $id")

        return media.toResponse()
    }

    fun getAllMedia(): List<MediaResponse> {
        return mediaRepository.findAll()
            .map { it.toResponse() }
    }

    fun getMediaByType(type: MediaType): List<MediaResponse> {
        return mediaRepository.findAll()
            .filter { it.type == type }
            .map { it.toResponse() }
    }

    fun searchMediaByTitle(keyword: String): List<MediaResponse> {
        return mediaRepository.findAll()
            .filter { it.title.contains(keyword, ignoreCase = true) }
            .map { it.toResponse() }
    }

    fun getMediaByTag(tagId: Long): List<MediaResponse> {
        tagRepository.findByIdOrNull(tagId)
            ?: throw IllegalArgumentException("Tag not found with id: $tagId")

        return mediaTagMappingRepository.findAll()
            .filter { it.tag.id == tagId }
            .map { it.media.toResponse() }
    }

    @Transactional
    fun updateMedia(id: Long, request: UpdateMediaRequest): MediaResponse {
        val media = mediaRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Media not found with id: $id")

        media.title = request.title
        media.year = request.year
        media.imageUrl = request.imageUrl

        return media.toResponse()
    }

    @Transactional
    fun deleteMedia(id: Long) {
        val media = mediaRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Media not found with id: $id")

        mediaTagMappingRepository.findAll()
            .filter { it.media.id == id }
            .forEach { mediaTagMappingRepository.delete(it) }

        mediaRepository.delete(media)
    }

    private fun Media.toResponse(): MediaResponse {
        val tags = mediaTagMappingRepository.findAll()
            .filter { it.media.id == this.id }
            .map { TagInfo(id = it.tag.id!!, name = it.tag.name) }

        return MediaResponse(
            id = id!!,
            externalId = externalId,
            type = type,
            title = title,
            year = year,
            imageUrl = imageUrl,
            tags = tags,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
