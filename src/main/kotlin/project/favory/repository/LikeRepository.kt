package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.Like

interface LikeRepository : JpaRepository<Like, Long> {
    fun existsByUserIdAndFavoryId(userId: Long, favoryId: Long): Boolean
    fun deleteByUserIdAndFavoryId(userId: Long, favoryId: Long)
}