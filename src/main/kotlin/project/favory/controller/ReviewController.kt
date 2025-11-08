package project.favory.controller

import org.springframework.web.bind.annotation.*
import project.favory.dto.review.request.CreateReviewRequest
import project.favory.dto.review.request.UpdateReviewRequest
import project.favory.dto.review.response.ReviewResponse
import project.favory.service.ReviewService

@RestController
@RequestMapping("/reviews")
class ReviewController(
    private val reviewService: ReviewService
) {

    @PostMapping
    fun createReview(@RequestBody request: CreateReviewRequest): ReviewResponse {
        return reviewService.createReview(request)
    }

    @GetMapping("/media/{mediaId}")
    fun getReviewsByMedia(@PathVariable mediaId: Long): List<ReviewResponse> {
        return reviewService.getReviewsByMedia(mediaId)
    }

    @PutMapping("/{id}")
    fun updateReview(
        @PathVariable id: Long,
        @RequestBody request: UpdateReviewRequest
    ): ReviewResponse {
        return reviewService.updateReview(id, request)
    }

    @DeleteMapping("/{id}")
    fun deleteReview(@PathVariable id: Long) {
        reviewService.deleteReview(id)
    }
}
