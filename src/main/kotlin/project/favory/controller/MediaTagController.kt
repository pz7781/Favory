package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import project.favory.dto.mediatag.request.AddTagToMediaRequest
import project.favory.dto.mediatag.response.MediaTagMappingResponse
import project.favory.service.MediaTagService

@Tag(name = "MediaTag", description = "미디어 태그 연결 관리")
@RestController
@RequestMapping("/media-tags")
class MediaTagController(
    private val mediaTagService: MediaTagService
) {

    @Operation(summary = "미디어에 태그 추가")
    @PostMapping
    fun addTagToMedia(@RequestBody request: AddTagToMediaRequest): MediaTagMappingResponse {
        return mediaTagService.addTagToMedia(request)
    }

    @Operation(summary = "미디어에서 태그 제거")
    @DeleteMapping("/media/{mediaId}/tag/{tagId}")
    fun removeTagFromMedia(
        @PathVariable mediaId: Long,
        @PathVariable tagId: Long
    ) {
        mediaTagService.removeTagFromMedia(mediaId, tagId)
    }
}
