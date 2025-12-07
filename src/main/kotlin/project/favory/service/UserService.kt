package project.favory.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.common.exception.UserNotFoundException
import project.favory.dto.user.request.UpdateUserRequest
import project.favory.dto.user.response.UserResponse
import project.favory.entity.User
import project.favory.repository.UserRepository

// 유저 정보 조회, 프로필 수정, 프로필 공개

@Service
class UserService (
    private val userRepository: UserRepository
){

    // 반복 패턴 캡슐화 - 아이디 존재 확인
    private fun findByIdOrThrow(id: Long): User =
        userRepository.findById(id).orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

    // 닉네임으로 조회
    @Transactional(readOnly = true)
    fun getByNickname(nickname: String): UserResponse =
        userRepository.findByNickname(nickname)
            ?.toResponse()
            ?: throw NoSuchElementException("사용자를 찾을 수 없습니다.")

    // 이메일로 조회
    @Transactional(readOnly = true)
    fun getByEmail(email: String): UserResponse =
        userRepository.findByEmail(email)
            ?.toResponse()
            ?: throw UserNotFoundException()

    // 아이디로 조회
    @Transactional(readOnly = true)
    fun getById(id: Long): UserResponse =
        findByIdOrThrow(id).toResponse()

    // 프로필 수정
    @Transactional
    fun update(id: Long, req: UpdateUserRequest): UserResponse {
        val user = findByIdOrThrow(id)

        req.nickname?.let { newNick ->
            if (newNick.isNotBlank() && newNick != user.nickname) {
                if (userRepository.existsByNickname(newNick)) {
                    throw IllegalArgumentException("이미 사용 중인 닉네임입니다.")
                }
                user.nickname = newNick
            }
        }

        req.profileImageUrl?.let { user.profileImageUrl = it }
        req.profileMessage?.let { user.profileMessage = it }

        return user.toResponse()
    }

    // 아이디 삭제
    @Transactional
    fun delete(id: Long) {
        val user = findByIdOrThrow(id)
        userRepository.delete(user)
    }

    private fun User.toResponse() = UserResponse(
        id = this.id!!,
        email = this.email,
        nickname = this.nickname,
        profileImageUrl = this.profileImageUrl,
        profileMessage = this.profileMessage
    )
}