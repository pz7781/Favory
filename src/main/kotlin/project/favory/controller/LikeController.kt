package project.favory.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import project.favory.service.LikeService
import project.favory.service.UserService

@Tag(name = "Like", description = "Favory 좋아요 등록/취소")
@RestController
@RequestMapping("/favories")
class LikeController(
    private val likeService: LikeService,
    private val userService: UserService
) {

    @PostMapping("/like/{favoryId}")
    fun toggleLike(@PathVariable favoryId: Long): ResponseEntity<Map<String, Boolean>> {

        val user = userService.getCurrentUserOrThrow()

        val liked = likeService.toggleLike(user.id!!, favoryId)

        return ResponseEntity.ok(mapOf("liked" to liked))
    }
}