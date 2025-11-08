package project.favory.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.mediatag.request.AddTagToMediaRequest
import project.favory.dto.mediatag.response.MediaTagMappingResponse
import project.favory.entity.MediaTagMapping
import project.favory.repository.MediaRepository
import project.favory.repository.MediaTagMappingRepository
import project.favory.repository.TagRepository

@Service
@Transactional(readOnly = true)
class MediaTagService(
    private val mediaTagMappingRepository: MediaTagMappingRepository,
    private val mediaRepository: MediaRepository,
    private val tagRepository: TagRepository
) {

    @Transactional
    fun addTagToMedia(request: AddTagToMediaRequest): MediaTagMappingResponse {
        val media = mediaRepository.findByIdOrNull(request.mediaId)
            ?: throw IllegalArgumentException("Media not found with id: ${request.mediaId}")

        val tag = tagRepository.findByIdOrNull(request.tagId)
            ?: throw IllegalArgumentException("Tag not found with id: ${request.tagId}")

        val existingMapping = mediaTagMappingRepository.findAll()
            .find { it.media.id == request.mediaId && it.tag.id == request.tagId }

        if (existingMapping != null) {
            throw IllegalArgumentException("Tag already added to this media")
        }

        val mapping = MediaTagMapping(
            media = media,
            tag = tag
        )

        val savedMapping = mediaTagMappingRepository.save(mapping)
        return savedMapping.toResponse()
    }

    fun getTagsByMedia(mediaId: Long): List<MediaTagMappingResponse> {
        mediaRepository.findByIdOrNull(mediaId)
            ?: throw IllegalArgumentException("Media not found with id: $mediaId")

        return mediaTagMappingRepository.findAll()
            .filter { it.media.id == mediaId }
            .map { it.toResponse() }
    }

    fun getMediaByTag(tagId: Long): List<MediaTagMappingResponse> {
        tagRepository.findByIdOrNull(tagId)
            ?: throw IllegalArgumentException("Tag not found with id: $tagId")

        return mediaTagMappingRepository.findAll()
            .filter { it.tag.id == tagId }
            .map { it.toResponse() }
    }

    @Transactional
    fun removeTagFromMedia(mediaId: Long, tagId: Long) {
        val mapping = mediaTagMappingRepository.findAll()
            .find { it.media.id == mediaId && it.tag.id == tagId }
            ?: throw IllegalArgumentException("Mapping not found")

        mediaTagMappingRepository.delete(mapping)
    }

    private fun MediaTagMapping.toResponse() = MediaTagMappingResponse(
        id = id!!,
        mediaId = media.id!!,
        mediaTitle = media.title,
        tagId = tag.id!!,
        tagName = tag.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
