package zhi.yest.vk.communityscanner.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import zhi.yest.vk.communityscanner.domain.Request
import zhi.yest.vk.communityscanner.domain.User
import zhi.yest.vk.communityscanner.processing.filterByFields
import zhi.yest.vk.communityscanner.processing.findInteresting
import zhi.yest.vk.communityscanner.vk.GroupService

@RestController
@RequestMapping("people")
class PeopleController(private val groupService: GroupService) {
    @PostMapping(produces = ["application/stream+json"])
    fun getPeople(@RequestBody request: Request): Flux<User> {
        return groupService.getMembers(request.communities)
                .findInteresting(request.communities.size)
                .filterByFields { request.peopleFilters }
    }
}
