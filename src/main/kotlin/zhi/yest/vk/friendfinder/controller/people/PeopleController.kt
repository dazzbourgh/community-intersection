package zhi.yest.vk.friendfinder.controller.people

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.vk.DelayingRequestSender
import zhi.yest.vk.friendfinder.vk.UserService

@RestController
@RequestMapping("people")
class PeopleController(private val userService: UserService,
                       private val delayingRequestSender: DelayingRequestSender) {
    @FlowPreview
    @PostMapping(produces = ["application/stream+json"])
    suspend fun findInteresting(@RequestBody request: Request) = request.groupIds
            .flatMap {
                delayingRequestSender.request {
                    userService.search(it, request.fields)
                }
            }
            .groupBy { it.id }[request.groupIds.size]
            ?.asFlow() ?: emptyFlow()
}
