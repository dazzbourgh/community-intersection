package zhi.yest.vk.friendfinder.controller.test

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController {
    @GetMapping
    fun test(@RequestParam code: String): String {
        return code
    }

    @GetMapping("/after")
    fun after(): String {
        return "authenticated"
    }
}
