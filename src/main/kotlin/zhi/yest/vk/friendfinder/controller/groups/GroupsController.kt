package zhi.yest.vk.friendfinder.controller.groups

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactive.publish
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.*
import zhi.yest.vk.friendfinder.vk.DelayingRequestSender
import zhi.yest.vk.friendfinder.vk.GroupsService
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("groups")
@CrossOrigin(value = ["*"],
        allowedHeaders = ["*"],
        maxAge = 3600)
class GroupsController(private val groupsService: GroupsService,
                       private val delayingRequestSender: DelayingRequestSender) {
    private val scope = CoroutineScope(EmptyCoroutineContext)

    @GetMapping("/{id}")
    fun getGroupInfo(@PathVariable("id") groupId: String, authentication: OAuth2AuthenticationToken) = scope.publish {
        send(delayingRequestSender.request {
            groupsService.findById(groupId, authentication.principal)
        })
    }
}