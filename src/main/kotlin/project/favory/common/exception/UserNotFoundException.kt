package project.favory.common.exception

class UserNotFoundException(
    override val message: String = "사용자를 찾을 수 없습니다."
) : RuntimeException(message){
}