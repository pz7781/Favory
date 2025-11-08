package project.favory.controller

import org.springframework.web.bind.annotation.*
import project.favory.dto.tag.request.CreateTagRequest
import project.favory.dto.tag.response.TagResponse
import project.favory.service.TagService

@RestController
@RequestMapping("/tags")
class TagController(
    private val tagService: TagService
) {

    @PostMapping
    fun createTag(@RequestBody request: CreateTagRequest): TagResponse {
        return tagService.createTag(request)
    }

    @GetMapping
    fun getAllTags(@RequestParam(required = false) name: String?): List<TagResponse> {
        return if (name != null) {
            tagService.searchTagsByName(name)
        } else {
            tagService.getAllTags()
        }
    }
}
