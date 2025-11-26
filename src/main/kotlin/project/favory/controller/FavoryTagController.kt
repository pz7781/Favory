package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import project.favory.dto.favorytag.request.AddTagToFavoryRequest
import project.favory.dto.favorytag.response.FavoryTagMappingResponse
import project.favory.service.FavoryTagService

@Tag(name = "FavoryTag", description = "Favory 태그 연결 관리")
@RestController
@RequestMapping("/favory-tags")
class FavoryTagController(
    private val favoryTagService: FavoryTagService
) {

    @Operation(summary = "Favory에 태그 추가")
    @PostMapping
    fun addTagToFavory(@RequestBody request: AddTagToFavoryRequest): FavoryTagMappingResponse {
        return favoryTagService.addTagToFavory(request)
    }

    @Operation(summary = "Favory의 태그 목록 조회")
    @GetMapping("/favory/{favoryId}")
    fun getTagsByFavory(@PathVariable favoryId: Long): List<FavoryTagMappingResponse> {
        return favoryTagService.getTagsByFavory(favoryId)
    }

    @Operation(summary = "태그별 Favory 목록 조회")
    @GetMapping("/tag/{tagId}")
    fun getFavoriesByTag(@PathVariable tagId: Long): List<FavoryTagMappingResponse> {
        return favoryTagService.getFavoriesByTag(tagId)
    }

    @Operation(summary = "Favory에서 태그 제거")
    @DeleteMapping("/favory/{favoryId}/tag/{tagId}")
    fun removeTagFromFavory(
        @PathVariable favoryId: Long,
        @PathVariable tagId: Long
    ) {
        favoryTagService.removeTagFromFavory(favoryId, tagId)
    }
}
