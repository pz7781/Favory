package project.favory.common.exception

interface DuplicateFieldException {
    val field: String
    val message: String
}

class DuplicateEmailException(
    override val message: String = "이미 사용 중인 이메일입니다."
) : RuntimeException(message), DuplicateFieldException {
    override val field: String = "email"
}

class DuplicateNicknameException(
    override val message: String = "이미 사용 중인 닉네임입니다."
) : RuntimeException(message), DuplicateFieldException {
    override val field: String = "nickname"
}