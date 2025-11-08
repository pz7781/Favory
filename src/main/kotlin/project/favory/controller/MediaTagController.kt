package project.favory.controller

import org.springframework.web.bind.annotation.*
import project.favory.dto.mediatag.request.AddTagToMediaRequest
import project.favory.dto.mediatag.response.MediaTagMappingResponse
import project.favory.service.MediaTagService

@RestController
@RequestMapping("/media-tags")
class MediaTagController(
    private val mediaTagService: MediaTagService
) {

    @PostMapping
    fun addTagToMedia(@RequestBody request: AddTagToMediaRequest): MediaTagMappingResponse {
        return mediaTagService.addTagToMedia(request)
    }

    @DeleteMapping("/media/{mediaId}/tag/{tagId}")
    fun removeTagFromMedia(
        @PathVariable mediaId: Long,
        @PathVariable tagId: Long
    ) {
        mediaTagService.removeTagFromMedia(mediaId, tagId)
    }
}
