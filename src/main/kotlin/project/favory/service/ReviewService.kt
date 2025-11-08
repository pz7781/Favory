package project.favory.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.review.request.CreateReviewRequest
import project.favory.dto.review.request.UpdateReviewRequest
import project.favory.dto.review.response.ReviewResponse
import project.favory.entity.Review
import project.favory.repository.MediaRepository
import project.favory.repository.ReviewRepository
import project.favory.repository.UserRepository
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val mediaRepository: MediaRepository
) {

    @Transactional
    fun createReview(request: CreateReviewRequest): ReviewResponse {
        val user = userRepository.findByIdOrNull(request.userId)
            ?: throw IllegalArgumentException("User not found with id: ${request.userId}")

        val media = mediaRepository.findByIdOrNull(request.mediaId)
            ?: throw IllegalArgumentException("Media not found with id: ${request.mediaId}")

        val review = Review(
            user = user,
            media = media,
            title = request.title,
            content = request.content,
            deletedAt = null
        )

        val savedReview = reviewRepository.save(review)
        return savedReview.toResponse()
    }

    fun getReview(id: Long): ReviewResponse {
        val review = reviewRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Review not found with id: $id")

        if (review.deletedAt != null) {
            throw IllegalArgumentException("Review is deleted")
        }

        return review.toResponse()
    }

    fun getAllReviews(): List<ReviewResponse> {
        return reviewRepository.findAll()
            .filter { it.deletedAt == null }
            .map { it.toResponse() }
    }

    fun getReviewsByMedia(mediaId: Long): List<ReviewResponse> {
        mediaRepository.findByIdOrNull(mediaId)
            ?: throw IllegalArgumentException("Media not found with id: $mediaId")

        return reviewRepository.findAll()
            .filter { it.media.id == mediaId && it.deletedAt == null }
            .map { it.toResponse() }
    }

    fun getReviewsByUser(userId: Long): List<ReviewResponse> {
        userRepository.findByIdOrNull(userId)
            ?: throw IllegalArgumentException("User not found with id: $userId")

        return reviewRepository.findAll()
            .filter { it.user.id == userId && it.deletedAt == null }
            .map { it.toResponse() }
    }

    @Transactional
    fun updateReview(id: Long, request: UpdateReviewRequest): ReviewResponse {
        val review = reviewRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Review not found with id: $id")

        if (review.deletedAt != null) {
            throw IllegalArgumentException("Review is deleted")
        }

        review.title = request.title
        review.content = request.content

        return review.toResponse()
    }

    @Transactional
    fun deleteReview(id: Long) {
        val review = reviewRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Review not found with id: $id")

        review.deletedAt = LocalDateTime.now()
    }

    private fun Review.toResponse() = ReviewResponse(
        id = id!!,
        userId = user.id!!,
        userNickname = user.nickname,
        mediaId = media.id!!,
        mediaTitle = media.title,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}
