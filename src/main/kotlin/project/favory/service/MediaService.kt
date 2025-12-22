package project.favory.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.media.request.CreateMediaRequest
import project.favory.dto.media.request.UpdateMediaRequest
import project.favory.dto.media.response.MediaExistsResponse
import project.favory.dto.media.response.MediaResponse
import project.favory.entity.Media
import project.favory.entity.MediaType
import project.favory.repository.MediaRepository
import project.favory.common.exception.*

@Service
@Transactional(readOnly = true)
class MediaService(
    private val mediaRepository: MediaRepository
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
            type = request.mediaType,
            title = request.title,
            creator = request.creator,
            year = request.year,
            imageUrl = request.imageUrl
        )

        val savedMedia = mediaRepository.save(media)

        return savedMedia.toResponse()
    }

    fun getMedia(id: Long): MediaResponse {
        val media = mediaRepository.findByIdOrNull(id)
            ?: throw NotFoundException(ErrorCode.MEDIA_NOT_FOUND)

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

    @Transactional
    fun updateMedia(id: Long, request: UpdateMediaRequest): MediaResponse {
        val media = mediaRepository.findByIdOrNull(id)
            ?: throw NotFoundException(ErrorCode.MEDIA_NOT_FOUND)

        media.title = request.title
        media.creator = request.creator
        media.year = request.year
        media.imageUrl = request.imageUrl

        return media.toResponse()
    }

    @Transactional
    fun deleteMedia(id: Long) {
        val media = mediaRepository.findByIdOrNull(id)
            ?: throw NotFoundException(ErrorCode.MEDIA_NOT_FOUND)

        mediaRepository.delete(media)
    }

    private fun Media.toResponse(): MediaResponse {
        return MediaResponse(
            id = id!!,
            externalId = externalId,
            mediaType = type,
            title = title,
            creator = creator,
            year = year,
            imageUrl = imageUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
