package project.favory.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.common.exception.ErrorCode
import project.favory.common.exception.NotFoundException
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

        val favory = favoryRepository.findByIdWithPessimisticLock(favoryId)
            ?: throw NotFoundException(ErrorCode.FAVORY_NOT_FOUND)

        if (likeRepository.existsByUserIdAndFavoryId(userId, favoryId)) {
            likeRepository.deleteByUserIdAndFavoryId(userId, favoryId)
            favoryRepository.decreaseLikeCount(favoryId)
            return false
        }

        val user = userRepository.getReferenceById(userId)

        likeRepository.save(Like(user = user, favory = favory))
        favoryRepository.increaseLikeCount(favoryId)

        return true
    }
}