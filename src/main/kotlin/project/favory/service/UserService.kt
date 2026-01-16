package project.favory.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import project.favory.common.exception.BadRequestException
import project.favory.common.exception.ErrorCode
import project.favory.common.exception.NotFoundException
import project.favory.dto.user.request.UpdateUserRequest
import project.favory.dto.user.response.UserResponse
import project.favory.entity.User
import project.favory.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val s3Service: S3Service
) {

    @Transactional(readOnly = true)
    fun getMe(): UserResponse = getCurrentUserOrThrow().toResponse()

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
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
    }

    private fun findByIdOrThrow(id: Long): User =
        userRepository.findById(id).orElseThrow { NotFoundException(ErrorCode.USER_NOT_FOUND) }

    @Transactional(readOnly = true)
    fun getByNickname(nickname: String): UserResponse =
        userRepository.findByNickname(nickname.trim().lowercase())
            ?.toResponse()
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)

    @Transactional(readOnly = true)
    fun getByEmail(email: String): UserResponse =
        userRepository.findByEmail(email)
            ?.toResponse()
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)

    @Transactional(readOnly = true)
    fun getById(id: Long): UserResponse = findByIdOrThrow(id).toResponse()

    @Transactional
    fun update(id: Long, req: UpdateUserRequest): UserResponse {
        val user = findByIdOrThrow(id)

        req.nickname?.let { raw ->
            val newNick = raw.trim().lowercase()

            if (newNick.isNotBlank() && newNick != user.nickname) {
                if (userRepository.existsByNickname(newNick)) {
                    throw BadRequestException(ErrorCode.DUPLICATE_NICKNAME, field = "nickname")
                }
                user.nickname = newNick
            }
        }
        req.profileMessage?.let { user.profileMessage = it }

        return user.toResponse()
    }

    @Transactional
    fun uploadProfileImage(id: Long, file: MultipartFile): String {
        validateProfileImage(file)

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

    private fun validateProfileImage(file: MultipartFile) {
        // 파일 크기 검증 (5MB)
        val maxSizeInBytes = 5 * 1024 * 1024L
        if (file.size > maxSizeInBytes) {
            throw BadRequestException(ErrorCode.FILE_SIZE_EXCEEDED)
        }

        // 파일 확장자 검증
        val allowedFormats = setOf("jpg", "jpeg", "png", "ico", "webp")
        val extension = file.originalFilename?.substringAfterLast(".", "")?.lowercase()

        if (extension !in allowedFormats) {
            throw BadRequestException(ErrorCode.INVALID_FILE_FORMAT)
        }
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