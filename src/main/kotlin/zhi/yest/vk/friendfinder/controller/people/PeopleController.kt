package zhi.yest.vk.friendfinder.controller.people

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactive.publish
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zhi.yest.vk.friendfinder.config.security.dto.VkUserInfo
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.vk.DelayingRequestSender
import zhi.yest.vk.friendfinder.vk.UserService
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("people")
class PeopleController(private val userService: UserService,
                       private val delayingRequestSender: DelayingRequestSender) {
    private val scope = CoroutineScope(EmptyCoroutineContext)

    @PostMapping(produces = ["application/stream+json"])
    fun findInteresting(@RequestBody request: Request, authentication: OAuth2AuthenticationToken) = scope.publish {
        request.groupIds
                .flatMap {
                    delayingRequestSender.request {
                        userService.search(it, request.fields, authentication.principal)
                    }
                }
                .groupingBy { it }
                .eachCount()
                .filter { it.value == request.groupIds.size }
                .forEach { (user, _) -> send(user) }
    }

    @GetMapping("me")
    fun me(authentication: OAuth2AuthenticationToken): VkUserInfo {
        val attributes = authentication.principal.attributes
        return VkUserInfo(attributes["id"] as Long,
                attributes["firstName"] as String,
                attributes["lastName"] as String)
    }
}
