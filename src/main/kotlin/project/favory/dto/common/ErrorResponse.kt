package project.favory.dto.common

data class ErrorResponse(
    val message: String,
    val details: Map<String, FieldErrorDetail>? = null
) {
}