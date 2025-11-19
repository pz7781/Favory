package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import project.favory.dto.common.PageResponse
import project.favory.dto.review.request.CreateReviewRequest
import project.favory.dto.review.request.UpdateReviewRequest
import project.favory.dto.review.response.ReviewResponse
import project.favory.service.ReviewService

@Tag(name = "Review", description = "리뷰 생성/조회/수정/삭제")
@RestController
@RequestMapping("/reviews")
class ReviewController(
    private val reviewService: ReviewService
) {

    @Operation(summary = "리뷰 생성")
    @PostMapping
    fun createReview(@RequestBody request: CreateReviewRequest): ReviewResponse {
        return reviewService.createReview(request)
    }

    @Operation(summary = "전체 리뷰 조회 (페이징, 정렬: latest/oldest)")
    @GetMapping
    fun getAllReviews(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "latest") sort: String
    ): PageResponse<ReviewResponse> {
        return reviewService.getAllReviews(page, size, sort)
    }

    @Deprecated("미사용")
    @Operation(summary = "미디어별 리뷰 조회")
    @GetMapping("/media/{mediaId}")
    fun getReviewsByMedia(@PathVariable mediaId: Long): List<ReviewResponse> {
        return reviewService.getReviewsByMedia(mediaId)
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping("/{id}")
    fun updateReview(
        @PathVariable id: Long,
        @RequestBody request: UpdateReviewRequest
    ): ReviewResponse {
        return reviewService.updateReview(id, request)
    }

    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{id}")
    fun deleteReview(@PathVariable id: Long) {
        reviewService.deleteReview(id)
    }
}
