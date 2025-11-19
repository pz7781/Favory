package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import project.favory.dto.tag.request.CreateTagRequest
import project.favory.dto.tag.response.TagResponse
import project.favory.service.TagService

@Tag(name = "Tag", description = "태그 생성/조회")
@RestController
@RequestMapping("/tags")
class TagController(
    private val tagService: TagService
) {

    @Operation(summary = "태그 생성")
    @PostMapping
    fun createTag(@RequestBody request: CreateTagRequest): TagResponse {
        return tagService.createTag(request)
    }

    @Operation(summary = "태그 목록 조회 (이름 검색)")
    @GetMapping
    fun getAllTags(@RequestParam(required = false) name: String?): List<TagResponse> {
        return if (name != null) {
            tagService.searchTagsByName(name)
        } else {
            tagService.getAllTags()
        }
    }
}
