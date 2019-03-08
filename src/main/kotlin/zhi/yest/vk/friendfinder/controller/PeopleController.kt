package zhi.yest.vk.friendfinder.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
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
    @PostMapping(produces = ["application/stream+json"])
    fun getPeople(@RequestBody request: Request): Flux<DownloadableDataDto<User>> {
        return groupService.getMembers(request.communities)
                .findInteresting(request.communities.size)
                .filterByFields { request.peopleFilters }
                .filterFaceless()
    }
}
