package zhi.yest.vk.friendfinder.controller.people

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactive.publish
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
    fun findInteresting(@RequestBody request: Request) = scope.publish {
        request.groupIds
                .flatMap {
                    delayingRequestSender.request {
                        userService.search(it, request.fields)
                    }
                }
                .groupingBy { it }
                .eachCount()
                .filter { it.value == request.groupIds.size }
                .forEach { (user, _) -> send(user) }
    }
}
