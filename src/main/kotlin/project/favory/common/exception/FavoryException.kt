package project.favory.common.exception

open class FavoryException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    val field: String? = null
) : RuntimeException(message)

class BadRequestException(
    errorCode: ErrorCode = ErrorCode.INVALID_INPUT,
    message: String = errorCode.message,
    field: String? = null
) : FavoryException(errorCode, message, field)

class UnauthorizedException(
    errorCode: ErrorCode = ErrorCode.NOT_AUTHENTICATED,
    message: String = errorCode.message
) : FavoryException(errorCode, message)

class ForbiddenException(
    errorCode: ErrorCode = ErrorCode.ACCESS_DENIED,
    message: String = errorCode.message
) : FavoryException(errorCode, message)

class NotFoundException(
    errorCode: ErrorCode,
    message: String = errorCode.message
) : FavoryException(errorCode, message)

class InternalServerException(
    errorCode: ErrorCode = ErrorCode.INTERNAL_ERROR,
    message: String = errorCode.message
) : FavoryException(errorCode, message)
