package project.favory.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.user.CreateUserRequest
import project.favory.dto.user.UserResponse
import project.favory.entity.User
import project.favory.repository.UserRepository

@Service
class UserService (
    private val userRepository: UserRepository
){
    @Transactional
    fun create(req: CreateUserRequest): UserResponse {
        if (userRepository.existsByEmail(req.email)) {
            throw IllegalArgumentException("이미 사용 중인 이메일입니다.")
        }
        val saved = userRepository.save(
            User(
                email = req.email,
                password = req.password,
                nickname = req.nickname,
                profileImageUrl = req.profileImageUrl,
                profileMessage = req.profileMessage
            )
        )
        return saved.toResponse()
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): UserResponse =
        userRepository.findById(id)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }
            .toResponse()

    @Transactional(readOnly = true)
    fun getAll(): List<UserResponse> =
        userRepository.findAll().map { it.toResponse() }

    @Transactional
    fun delete(id: Long) {
        if (!userRepository.existsById(id)) {
            throw NoSuchElementException("삭제할 사용자가 없습니다.")
        }
        userRepository.deleteById(id)
    }

    private fun User.toResponse() = UserResponse(
        id = this.id!!,
        email = this.email,
        nickname = this.nickname,
        profileImageUrl = this.profileImageUrl,
        profileMessage = this.profileMessage
    )
}