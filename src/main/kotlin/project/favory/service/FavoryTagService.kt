package project.favory.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.favorytag.request.AddTagToFavoryRequest
import project.favory.dto.favorytag.response.FavoryTagMappingResponse
import project.favory.entity.FavoryTagMapping
import project.favory.repository.FavoryRepository
import project.favory.repository.FavoryTagMappingRepository
import project.favory.repository.TagRepository

@Service
@Transactional(readOnly = true)
class FavoryTagService(
    private val favoryTagMappingRepository: FavoryTagMappingRepository,
    private val favoryRepository: FavoryRepository,
    private val tagRepository: TagRepository
) {

    @Transactional
    fun addTagToFavory(request: AddTagToFavoryRequest): FavoryTagMappingResponse {
        val favory = favoryRepository.findByIdOrNull(request.favoryId)
            ?: throw IllegalArgumentException("Favory not found with id: ${request.favoryId}")

        val tag = tagRepository.findByIdOrNull(request.tagId)
            ?: throw IllegalArgumentException("Tag not found with id: ${request.tagId}")

        val existingMapping = favoryTagMappingRepository.findAllByFavoryId(request.favoryId)
            .find { it.tag.id == request.tagId }

        if (existingMapping != null) {
            throw IllegalArgumentException("Tag already added to this favory")
        }

        val mapping = FavoryTagMapping(
            favory = favory,
            tag = tag
        )

        val savedMapping = favoryTagMappingRepository.save(mapping)
        return savedMapping.toResponse()
    }

    fun getTagsByFavory(favoryId: Long): List<FavoryTagMappingResponse> {
        favoryRepository.findByIdOrNull(favoryId)
            ?: throw IllegalArgumentException("Favory not found with id: $favoryId")

        return favoryTagMappingRepository.findAllByFavoryId(favoryId)
            .map { it.toResponse() }
    }

    fun getFavoriesByTag(tagId: Long): List<FavoryTagMappingResponse> {
        tagRepository.findByIdOrNull(tagId)
            ?: throw IllegalArgumentException("Tag not found with id: $tagId")

        return favoryTagMappingRepository.findAll()
            .filter { it.tag.id == tagId }
            .map { it.toResponse() }
    }

    @Transactional
    fun removeTagFromFavory(favoryId: Long, tagId: Long) {
        val mapping = favoryTagMappingRepository.findAllByFavoryId(favoryId)
            .find { it.tag.id == tagId }
            ?: throw IllegalArgumentException("Mapping not found")

        favoryTagMappingRepository.delete(mapping)
    }

    private fun FavoryTagMapping.toResponse() = FavoryTagMappingResponse(
        id = id!!,
        favoryId = favory.id!!,
        favoryTitle = favory.title,
        tagId = tag.id!!,
        tagName = tag.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
