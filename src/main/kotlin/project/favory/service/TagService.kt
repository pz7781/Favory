package project.favory.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.tag.response.TagResponse
import project.favory.entity.Tag
import project.favory.repository.TagRepository

@Service
@Transactional(readOnly = true)
class TagService(
    private val tagRepository: TagRepository
) {

    fun getAllTags(): List<TagResponse> {
        return tagRepository.findAll()
            .map { it.toResponse() }
    }

    fun searchTagsByName(name: String): List<TagResponse> {
        return tagRepository.findAll()
            .filter { it.name.contains(name, ignoreCase = true) }
            .map { it.toResponse() }
    }

    private fun Tag.toResponse() = TagResponse(
        id = id!!,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
