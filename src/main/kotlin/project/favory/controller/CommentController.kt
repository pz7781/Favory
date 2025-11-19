package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import project.favory.dto.comment.request.CreateCommentRequest
import project.favory.dto.comment.request.UpdateCommentRequest
import project.favory.dto.comment.response.CommentResponse
import project.favory.service.CommentService

@Tag(name = "Comment", description = "댓글 생성/조회/수정/삭제")
@RestController
@RequestMapping("/comments")
class CommentController(
    private val commentService: CommentService
) {

    @Operation(summary = "댓글 생성")
    @PostMapping
    fun createComment(@RequestBody request: CreateCommentRequest): CommentResponse {
        return commentService.createComment(request)
    }

    @Operation(summary = "리뷰별 댓글 조회")
    @GetMapping("/review/{reviewId}")
    fun getCommentsByReview(@PathVariable reviewId: Long): List<CommentResponse> {
        return commentService.getCommentsByReview(reviewId)
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/{id}")
    fun updateComment(
        @PathVariable id: Long,
        @RequestBody request: UpdateCommentRequest
    ): CommentResponse {
        return commentService.updateComment(id, request)
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{id}")
    fun deleteComment(@PathVariable id: Long) {
        commentService.deleteComment(id)
    }
}
