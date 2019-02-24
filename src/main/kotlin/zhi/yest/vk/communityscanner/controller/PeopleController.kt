package zhi.yest.vk.communityscanner.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import zhi.yest.vk.communityscanner.domain.Request
import zhi.yest.vk.communityscanner.domain.User
import zhi.yest.vk.communityscanner.vk.GroupService
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("people")
class PeopleController(private val groupService: GroupService) {
    @PostMapping(produces = ["application/stream+json"])
    fun getPeople(@RequestBody request: Request): Flux<User> {
        /*
            0. Get members count for each community
            1. Use "users.search" for each community to fetch users
                and leverage "fields" param to filter
            2. Find users that exist in every community
            3. For each user get list of interesting pages and check if
                specific communities are above the bottom threshold

                A total of
                sum(Ci / 1000 * count) + N
                requests, where N is the amount of users
                subscribed to all the communities specified in request
         */
        val usersMap: MutableMap<User, Int> = ConcurrentHashMap()
        return request.communities
                .asSequence()
                // using distinct since new users may subscribe while batch searching
                .map { groupService.getMembers(it) }
                .reduce { acc, flux -> acc.concatWith(flux) }
                .filter { user ->
                    usersMap.compute(user) { _, value ->
                        if (value == null) 1 else value + 1
                    }
                    if (usersMap[user] == request.communities.size) {
                        return@filter user.let { interestingUser ->
                            request.peopleFilters
                                    ?.entries
                                    ?.map { interestingUser.fields[it.key] == it.value }
                                    ?.all { it } ?: true
                        }
                    }
                    false
                }
    }
}
