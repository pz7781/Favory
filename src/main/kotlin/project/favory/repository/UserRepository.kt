package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByNickname(nickname: String): User?
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun findByNicknameStartingWithIgnoreCase(nickname: String): List<User>
}
