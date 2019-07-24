package zhi.yest.vk.friendfinder.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import zhi.yest.vk.friendfinder.config.security.dto.VkException

@ControllerAdvice
class ExceptionHandlers {

    @ExceptionHandler(VkException::class)
    fun vkException(exception: VkException): ResponseEntity<String> {
        val status = if (exception.error == null) HttpStatus.INTERNAL_SERVER_ERROR else HttpStatus.NOT_FOUND
        return ResponseEntity(exception.message, status)
    }
}