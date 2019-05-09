package zhi.yest.vk.friendfinder.controller.people

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.reactive.publish
import org.reactivestreams.Publisher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("people")
class PeopleController(private val fetchUsers: suspend ProducerScope<DownloadableDataDto<out User>>.(Request) -> ReceiveChannel<DownloadableDataDto<out User>>,
                       private val filteringFunctionsSupplier: () -> List<(User) -> (Request) -> Boolean>) {
    private val scope = CoroutineScope(EmptyCoroutineContext)

    @ExperimentalCoroutinesApi
    @PostMapping(produces = ["application/stream+json"])
    fun findInteresting(@RequestBody request: Request): Publisher<DownloadableDataDto<out User>> = scope.publish {
        val userChannel = fetchUsers(request)
        val processedUsers = processUserDtos(userChannel,
                request,
                filteringFunctionsSupplier())
        for (processedUser in processedUsers) send(processedUser)
    }
}

@ExperimentalCoroutinesApi
private fun ProducerScope<DownloadableDataDto<out User>>.processUserDtos(userChannel: ReceiveChannel<DownloadableDataDto<out User>>,
                                                                         request: Request,
                                                                         processingFunctions: List<(User) -> (Request) -> Boolean>) = produce {
    for (userDto in userChannel) {
        if (userDto.data == null || processingFunctions.all { it(userDto.data)(request) }) send(userDto)
    }
}
