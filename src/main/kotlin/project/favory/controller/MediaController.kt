package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import project.favory.dto.media.request.CreateMediaRequest
import project.favory.dto.media.request.UpdateMediaRequest
import project.favory.dto.media.response.MediaExistsResponse
import project.favory.dto.media.response.MediaResponse
import project.favory.dto.media.response.MediaSearchResponse
import project.favory.entity.MediaType
import project.favory.service.MediaSearchService
import project.favory.service.MediaService

@Tag(name = "Media", description = "미디어 생성/조회/수정/삭제 및 외부 API 검색")
@RestController
@RequestMapping("/media")
class MediaController(
    private val mediaService: MediaService,
    private val mediaSearchService: MediaSearchService
) {

    @Operation(summary = "미디어 존재 여부 확인")
    @GetMapping("/exists")
    fun checkMediaExists(@RequestParam externalId: String): MediaExistsResponse {
        return mediaService.checkMediaExists(externalId)
    }

    @Operation(summary = "미디어 생성")
    @PostMapping
    fun createMedia(@RequestBody request: CreateMediaRequest): MediaResponse {
        return mediaService.createMedia(request)
    }

    @Operation(summary = "미디어 단건 조회")
    @GetMapping("/{id}")
    fun getMedia(@PathVariable id: Long): MediaResponse {
        return mediaService.getMedia(id)
    }

    @Deprecated("미사용")
    @Operation(summary = "미디어 목록 조회 (타입/키워드/태그별)")
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

    @Operation(summary = "미디어 수정")
    @PutMapping("/{id}")
    fun updateMedia(
        @PathVariable id: Long,
        @RequestBody request: UpdateMediaRequest
    ): MediaResponse {
        return mediaService.updateMedia(id, request)
    }

    @Operation(summary = "미디어 삭제")
    @DeleteMapping("/{id}")
    fun deleteMedia(@PathVariable id: Long) {
        mediaService.deleteMedia(id)
    }

    @Operation(summary = "외부 API 미디어 검색 (Media 등록시 사용)")
    @GetMapping("/search")
    fun searchMedia(
        @RequestParam keyword: String,
        @RequestParam type: MediaType,
        @RequestParam(defaultValue = "10") limit: Int
    ): MediaSearchResponse {
        return mediaSearchService.searchMedia(keyword, type, limit)
    }
}
