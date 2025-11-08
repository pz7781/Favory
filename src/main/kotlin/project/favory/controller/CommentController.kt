package project.favory.controller

import org.springframework.web.bind.annotation.*
import project.favory.dto.comment.request.CreateCommentRequest
import project.favory.dto.comment.request.UpdateCommentRequest
import project.favory.dto.comment.response.CommentResponse
import project.favory.service.CommentService

@RestController
@RequestMapping("/comments")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping
    fun createComment(@RequestBody request: CreateCommentRequest): CommentResponse {
        return commentService.createComment(request)
    }

    @GetMapping("/review/{reviewId}")
    fun getCommentsByReview(@PathVariable reviewId: Long): List<CommentResponse> {
        return commentService.getCommentsByReview(reviewId)
    }

    @PutMapping("/{id}")
    fun updateComment(
        @PathVariable id: Long,
        @RequestBody request: UpdateCommentRequest
    ): CommentResponse {
        return commentService.updateComment(id, request)
    }

    @DeleteMapping("/{id}")
    fun deleteComment(@PathVariable id: Long) {
        commentService.deleteComment(id)
    }
}
