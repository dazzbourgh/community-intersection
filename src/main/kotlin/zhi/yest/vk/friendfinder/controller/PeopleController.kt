package zhi.yest.vk.friendfinder.controller

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.reactive.publish
import org.reactivestreams.Publisher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto
import zhi.yest.vk.friendfinder.processing.filterByFields
import zhi.yest.vk.friendfinder.processing.filterFaceless
import zhi.yest.vk.friendfinder.processing.findInteresting
import zhi.yest.vk.friendfinder.vk.GroupService

@RestController
@RequestMapping("people")
class PeopleController(private val groupService: GroupService) {
    @ExperimentalCoroutinesApi
    @PostMapping(produces = ["application/stream+json"])
    fun findInteresting(@RequestBody request: Request): Publisher<DownloadableDataDto<User>> = GlobalScope.publish {
        request.communities
                .map { groupService.getMembers(it) }
                .reduce { acc, sequence -> acc + sequence }
                .findInteresting(request.communities.size)
                .filterByFields { request.peopleFilters }
                .filterFaceless()
                .forEach { send(DownloadableDataDto(it, 0)) }
    }
}
