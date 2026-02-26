package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import project.favory.config.swagger.SecurityNotRequired
import project.favory.dto.common.PageResponse
import project.favory.dto.favory.request.CreateFavoryRequest
import project.favory.dto.favory.request.UpdateFavoryRequest
import project.favory.dto.favory.response.FavoryResponse
import project.favory.entity.MediaType
import project.favory.service.FavoryService

@Tag(name = "Favory", description = "Favory 생성/조회/수정/삭제")
@RestController
@RequestMapping("/favories")
class FavoryController(
    private val favoryService: FavoryService
) {

    @Operation(summary = "Favory 생성")
    @PostMapping
    fun createFavory(@RequestBody request: CreateFavoryRequest): FavoryResponse {
        return favoryService.createFavory(request)
    }

    @Operation(summary = "전체 Favory 조회 (페이징, 정렬: latest/oldest/popular, 타입 필터)")
    @GetMapping
    fun getAllFavories(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "latest") sort: String,
        @RequestParam(required = false) type: MediaType?
    ): PageResponse<FavoryResponse> {
        return favoryService.getAllFavories(page, size, sort, type)
    }

    @Operation(summary = "Favory 단건 조회")
    @GetMapping("/{id}")
    fun getFavory(@PathVariable id: Long): FavoryResponse {
        return favoryService.getFavory(id)
    }

    @Operation(summary = "미디어별 Favory 조회")
    @GetMapping("/media/{mediaId}")
    fun getFavoriesByMedia(@PathVariable mediaId: Long): List<FavoryResponse> {
        return favoryService.getFavoriesByMedia(mediaId)
    }

    @Operation(summary = "특정 사용자가 작성한 Favory 조회 (페이징, 정렬, 타입 필터)")
    @GetMapping("/users/{nickname}")
    fun getFavoriesByUser(
        @PathVariable nickname: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "latest") sort: String,
        @RequestParam(required = false) type: MediaType?
    ): PageResponse<FavoryResponse> {
        return favoryService.getFavoriesByUser(nickname, page, size, sort, type)
    }

    @Operation(summary = "Favory 수정")
    @PutMapping("/{id}")
    fun updateFavory(
        @PathVariable id: Long,
        @RequestBody request: UpdateFavoryRequest
    ): FavoryResponse {
        return favoryService.updateFavory(id, request)
    }

    @Operation(summary = "Favory 삭제")
    @DeleteMapping("/{id}")
    fun deleteFavory(@PathVariable id: Long) {
        favoryService.deleteFavory(id)
    }
}
