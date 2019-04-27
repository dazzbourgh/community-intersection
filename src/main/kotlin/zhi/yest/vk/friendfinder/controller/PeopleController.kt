package zhi.yest.vk.friendfinder.controller

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.reactive.consumeEach
import kotlinx.coroutines.reactive.publish
import org.reactivestreams.Publisher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto
import zhi.yest.vk.friendfinder.processing.processUserDtos
import zhi.yest.vk.friendfinder.vk.GroupService

@RestController
@RequestMapping("people")
class PeopleController(private val groupService: GroupService) {
    @ExperimentalCoroutinesApi
    @PostMapping(produces = ["application/stream+json"])
    fun findInteresting(@RequestBody request: Request): Publisher<DownloadableDataDto<out User>> = GlobalScope.publish {
        val membersCountList = request.communities.map { groupService.getMembersCount(it) }
        val communitiesCountPairs = request.communities.zip(membersCountList)
        val totalMembersCount = membersCountList.sum()
        val userChannel = produceUserDtos(totalMembersCount) {
            groupService.getUsers(communitiesCountPairs)
        }
        val processedUsers = processUserDtos(userChannel, request)
        for (processedUser in processedUsers) send(processedUser)
    }
}

@ExperimentalCoroutinesApi
private fun ProducerScope<DownloadableDataDto<out User>>.produceUserDtos(totalMembersCount: Int,
                                                                         userSupplier: suspend () -> Flux<User>) = produce {
    userSupplier()
            .index()
            .consumeEach {
                val percentage = (it.t1 / totalMembersCount.toFloat())
                val userDto = if (percentage * 10 - (percentage * 10).toInt() < 0.1) {
                    DownloadableDataDto(null, percentage.toInt())
                } else DownloadableDataDto(it.t2, percentage.toInt())
                send(userDto)
            }
            .also {
                send(DownloadableDataDto(null, 100))
                close()
            }
}

private suspend fun GroupService.getUsers(communitiesCountPairs: List<Pair<Int, Int>>): Flux<User> {
    return communitiesCountPairs.map { getMembers(it.first, it.second) }
            .reduce { acc, flux -> acc.concatWith(flux) }
}
