package project.favory.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.comment.request.CreateCommentRequest
import project.favory.dto.comment.request.UpdateCommentRequest
import project.favory.dto.comment.response.CommentResponse
import project.favory.dto.common.PageResponse
import project.favory.entity.Comment
import project.favory.repository.CommentRepository
import project.favory.repository.FavoryRepository
import project.favory.repository.UserRepository
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentRepository: CommentRepository,
    private val favoryRepository: FavoryRepository,
    private val userRepository: UserRepository,
    private val authService: AuthService
) {

    @Transactional
    fun createComment(request: CreateCommentRequest): CommentResponse {
        val favory = favoryRepository.findByIdOrNull(request.favoryId)
            ?: throw IllegalArgumentException("Favory not found with id: ${request.favoryId}")

        val user = userRepository.findByIdOrNull(request.userId)
            ?: throw IllegalArgumentException("User not found with id: ${request.userId}")

        val comment = Comment(
            favory = favory,
            user = user,
            content = request.content
        )

        val savedComment = commentRepository.save(comment)
        return savedComment.toResponse()
    }

    fun getComment(id: Long): CommentResponse {
        val comment = commentRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Comment not found with id: $id")

        if (comment.deletedAt != null) {
            throw IllegalArgumentException("Comment is deleted")
        }

        return comment.toResponse()
    }

    fun getAllComments(): List<CommentResponse> {
        return commentRepository.findAll()
            .filter { it.deletedAt == null }
            .map { it.toResponse() }
    }

    fun getCommentsByFavory(
        favoryId: Long,
        page: Int,
        size: Int,
        sortBy: String = "latest"
    ): PageResponse<CommentResponse> {
        favoryRepository.findByIdOrNull(favoryId)
            ?: throw IllegalArgumentException("Favory not found with id: $favoryId")

        val sort = when (sortBy) {
            "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt")
            else -> Sort.by(Sort.Direction.DESC, "createdAt")
        }

        val pageable: Pageable = PageRequest.of(page, size, sort)
        val commentPage: Page<Comment> = commentRepository.findAllByFavoryIdAndDeletedAtIsNull(favoryId, pageable)

        val content = commentPage.content.map { it.toResponse() }

        return PageResponse(
            content = content,
            pageNumber = commentPage.number,
            pageSize = commentPage.size,
            totalElements = commentPage.totalElements,
            totalPages = commentPage.totalPages
        )
    }

    @Transactional
    fun updateComment(id: Long, request: UpdateCommentRequest): CommentResponse {
        val comment = commentRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Comment not found with id: $id")

        if (comment.deletedAt != null) {
            throw IllegalArgumentException("Comment is deleted")
        }

        authService.validateUser(comment.user.id!!)

        comment.content = request.content

        return comment.toResponse()
    }

    @Transactional
    fun deleteComment(id: Long) {
        val comment = commentRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Comment not found with id: $id")

        authService.validateUser(comment.user.id!!)

        comment.deletedAt = LocalDateTime.now()
    }

    private fun Comment.toResponse() = CommentResponse(
        id = id!!,
        favoryId = favory.id!!,
        userId = user.id!!,
        userNickname = user.nickname,
        userImageUrl = user.profileImageUrl,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}
