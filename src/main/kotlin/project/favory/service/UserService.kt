package project.favory.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import project.favory.dto.user.request.UpdateUserRequest
import project.favory.dto.user.response.UserResponse
import project.favory.entity.User
import project.favory.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val s3Service: S3Service
) {

    fun getCurrentUserOrNull(): User? {
        val auth = SecurityContextHolder.getContext().authentication ?: return null

        if (!auth.isAuthenticated || auth.principal == "anonymousUser") {
            return null
        }
        val email = auth.name ?: return null

        return userRepository.findByEmail(email)
    }

    fun getCurrentUserOrThrow(): User {
        return getCurrentUserOrNull()
            ?: throw java.util.NoSuchElementException("사용자를 찾을 수 없습니다.")
    }

    private fun findByIdOrThrow(id: Long): User =
        userRepository.findById(id).orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

    @Transactional(readOnly = true)
    fun getByNickname(nickname: String): UserResponse =
        userRepository.findByNickname(nickname)
            ?.toResponse()
            ?: throw NoSuchElementException("사용자를 찾을 수 없습니다.")

    @Transactional(readOnly = true)
    fun getByEmail(email: String): UserResponse =
        userRepository.findByEmail(email)
            ?.toResponse()
            ?: throw NoSuchElementException("사용자를 찾을 수 없습니다.")

    @Transactional(readOnly = true)
    fun getById(id: Long): UserResponse =
        findByIdOrThrow(id).toResponse()

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

    @Transactional
    fun uploadProfileImage(id: Long, file: MultipartFile): String {
        val user = findByIdOrThrow(id)
        val oldImageUrl = user.profileImageUrl

        val newImageUrl = s3Service.uploadFile(file, "user")
        user.profileImageUrl = newImageUrl

        oldImageUrl?.let { oldUrl ->
            try {
                s3Service.deleteFile(oldUrl)
            } catch (e: Exception) {
            }
        }

        return newImageUrl
    }

    @Transactional
    fun delete(id: Long) {
        val user = findByIdOrThrow(id)

        user.profileImageUrl?.let { imageUrl ->
            try {
                s3Service.deleteFile(imageUrl)
            } catch (e: Exception) {
            }
        }

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