package zhi.yest.communityintersection.peopleservice.controller

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zhi.yest.communityintersection.peopleservice.domain.Request
import zhi.yest.communityintersection.peopleservice.dto.VkUserInfo
import zhi.yest.communityintersection.peopleservice.vk.DelayingRequestSender
import zhi.yest.communityintersection.peopleservice.vk.UserService

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("v1/people")
class PeopleController(private val userService: UserService,
                       private val delayingRequestSender: DelayingRequestSender) {

    @PostMapping
    suspend fun findInteresting(@RequestBody request: Request, authentication: OAuth2User) =
            request.groupIds
                    .flatMap {
                        delayingRequestSender.request {
                            userService.search(it, request.fields, authentication)
                        }
                    }
                    .asSequence()
                    .groupingBy { it }
                    .eachCount()
                    .filter { it.value == request.groupIds.size }
                    .map { it.key }

    @GetMapping("me")
    fun me(authentication: OAuth2AuthenticationToken): VkUserInfo {
        val attributes = authentication.principal.attributes
        return VkUserInfo(attributes["id"] as Long,
                attributes["firstName"] as String,
                attributes["lastName"] as String)
    }
}
