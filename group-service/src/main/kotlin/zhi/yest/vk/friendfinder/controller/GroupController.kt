package zhi.yest.vk.friendfinder.controller

import kotlinx.coroutines.reactive.publish
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zhi.yest.vk.friendfinder.service.vk.DelayingRequestSender
import zhi.yest.vk.friendfinder.service.vk.GroupService

@RestController
@RequestMapping("v1/group")
class GroupController(private val groupService: GroupService,
                      private val delayingRequestSender: DelayingRequestSender) {

    //TODO: figure out the problem with suspending methods for controllers
    @GetMapping("/{id}")
    fun getGroupInfo(@PathVariable("id") groupId: String,
                     @AuthenticationPrincipal jwt: Jwt) = publish {
        delayingRequestSender.request {
            groupService.findById(groupId, jwt.tokenValue)
        }.also {
            send(it)
            close()
        }
    }
}
