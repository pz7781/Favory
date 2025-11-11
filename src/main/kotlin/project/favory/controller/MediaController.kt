package project.favory.controller

import org.springframework.web.bind.annotation.*
import project.favory.dto.media.request.CreateMediaRequest
import project.favory.dto.media.request.UpdateMediaRequest
import project.favory.dto.media.response.MediaResponse
import project.favory.entity.MediaType
import project.favory.service.MediaService

@RestController
@RequestMapping("/media")
class MediaController(
    private val mediaService: MediaService
) {

    @PostMapping
    fun createMedia(@RequestBody request: CreateMediaRequest): MediaResponse {
        return mediaService.createMedia(request)
    }

    @GetMapping("/{id}")
    fun getMedia(@PathVariable id: Long): MediaResponse {
        return mediaService.getMedia(id)
    }

    @GetMapping
    fun getAllMedia(
        @RequestParam(required = false) type: MediaType?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) tagId: Long?
    ): List<MediaResponse> {
        return when {
            type != null -> mediaService.getMediaByType(type)
            keyword != null -> mediaService.searchMediaByTitle(keyword)
            tagId != null -> mediaService.getMediaByTag(tagId)
            else -> mediaService.getAllMedia()
        }
    }

    @PutMapping("/{id}")
    fun updateMedia(
        @PathVariable id: Long,
        @RequestBody request: UpdateMediaRequest
    ): MediaResponse {
        return mediaService.updateMedia(id, request)
    }

    @DeleteMapping("/{id}")
    fun deleteMedia(@PathVariable id: Long) {
        mediaService.deleteMedia(id)
    }
}
