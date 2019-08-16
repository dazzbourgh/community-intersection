package zhi.yest.vk.friendfinder.controller

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zhi.yest.vk.friendfinder.domain.Group
import zhi.yest.vk.friendfinder.service.vk.DelayingRequestSender
import zhi.yest.vk.friendfinder.service.vk.GroupsService

@RestController
@RequestMapping("groups")
class GroupsController(private val groupsService: GroupsService,
                       private val delayingRequestSender: DelayingRequestSender) {

    @GetMapping("/{id}")
    suspend fun getGroupInfo(@PathVariable("id") groupId: String, jwt: Jwt): Group =
            delayingRequestSender.request {
                groupsService.findById(groupId, jwt.tokenValue)
            }
}
