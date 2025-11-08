package project.favory.dto.mediatag.request

data class AddTagToMediaRequest(
    val mediaId: Long,
    val tagId: Long
)
