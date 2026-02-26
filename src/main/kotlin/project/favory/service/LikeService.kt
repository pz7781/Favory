package project.favory.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.entity.Like
import project.favory.repository.FavoryRepository
import project.favory.repository.LikeRepository
import project.favory.repository.UserRepository

@Service
class LikeService(
    private val likeRepository: LikeRepository,
    private val favoryRepository: FavoryRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun toggleLike(userId: Long, favoryId: Long): Boolean {

        if (likeRepository.existsByUserIdAndFavoryId(userId, favoryId)) {
            likeRepository.deleteByUserIdAndFavoryId(userId, favoryId)
            favoryRepository.decreaseLikeCount(favoryId)
            return false
        }

        val user = userRepository.getReferenceById(userId)
        val favory = favoryRepository.getReferenceById(favoryId)

        likeRepository.save(Like(user = user, favory = favory))
        favoryRepository.increaseLikeCount(favoryId)

        return true
    }
}