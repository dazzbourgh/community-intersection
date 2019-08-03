package zhi.yest.vk.friendfinder.controller.groups

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zhi.yest.vk.friendfinder.domain.Group
import zhi.yest.vk.friendfinder.vk.DelayingRequestSender
import zhi.yest.vk.friendfinder.vk.GroupsService

@RestController
@RequestMapping("groups")
class GroupsController(private val groupsService: GroupsService,
                       private val delayingRequestSender: DelayingRequestSender) {

    @GetMapping("/{id}")
    suspend fun getGroupInfo(@PathVariable("id") groupId: String, token: OAuth2AuthenticationToken): Group =
            delayingRequestSender.request {
                groupsService.findById(groupId, token)
            }
}