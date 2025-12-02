package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import project.favory.dto.tag.response.TagResponse
import project.favory.service.TagService

@Tag(name = "Tag", description = "태그 생성/조회")
@RestController
@RequestMapping("/tags")
class TagController(
    private val tagService: TagService
) {

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
