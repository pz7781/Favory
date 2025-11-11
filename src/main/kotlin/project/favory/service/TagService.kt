package project.favory.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.tag.request.CreateTagRequest
import project.favory.dto.tag.request.UpdateTagRequest
import project.favory.dto.tag.response.TagResponse
import project.favory.entity.Tag
import project.favory.repository.TagRepository

@Service
@Transactional(readOnly = true)
class TagService(
    private val tagRepository: TagRepository
) {

    @Transactional
    fun createTag(request: CreateTagRequest): TagResponse {
        tagRepository.findAll().find { it.name == request.name }?.let {
            throw IllegalArgumentException("Tag with name '${request.name}' already exists")
        }

        val tag = Tag(
            name = request.name
        )

        val savedTag = tagRepository.save(tag)
        return savedTag.toResponse()
    }

    fun getTag(id: Long): TagResponse {
        val tag = tagRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Tag not found with id: $id")

        return tag.toResponse()
    }

    fun getAllTags(): List<TagResponse> {
        return tagRepository.findAll()
            .map { it.toResponse() }
    }

    fun searchTagsByName(name: String): List<TagResponse> {
        return tagRepository.findAll()
            .filter { it.name.contains(name, ignoreCase = true) }
            .map { it.toResponse() }
    }

    @Transactional
    fun updateTag(id: Long, request: UpdateTagRequest): TagResponse {
        val tag = tagRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Tag not found with id: $id")

        tagRepository.findAll().find { it.name == request.name && it.id != id }?.let {
            throw IllegalArgumentException("Tag with name '${request.name}' already exists")
        }

        tag.name = request.name

        return tag.toResponse()
    }

    @Transactional
    fun deleteTag(id: Long) {
        val tag = tagRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Tag not found with id: $id")

        tagRepository.delete(tag)
    }

    private fun Tag.toResponse() = TagResponse(
        id = id!!,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
