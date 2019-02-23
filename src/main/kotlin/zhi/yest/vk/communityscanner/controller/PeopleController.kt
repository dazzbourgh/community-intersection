package zhi.yest.vk.communityscanner.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zhi.yest.vk.communityscanner.domain.Request
import zhi.yest.vk.communityscanner.domain.User
import zhi.yest.vk.communityscanner.vk.GroupService

@RestController
@RequestMapping("people")
class PeopleController(private val groupService: GroupService) {
    @PostMapping
    fun getPeople(@RequestBody request: Request): List<User> {
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
        val interestingUsers = mutableListOf<User>()
        val usersMap = mutableMapOf<User, Int>()
        request.communities
                .asSequence()
                .map { groupService.getMembers(it) }.forEach { userList ->
                    userList.forEach {
                        usersMap.compute(it) { _, value ->
                            if (value == null) 1 else {
                                if (value + 1 == request.communities.size) {
                                    interestingUsers.add(it)
                                }
                                value + 1
                            }
                        }
                    }
                }
        // TODO: filter by all fields, not only sex
        return interestingUsers
                .filter { user ->
                    request.peopleFilters
                            ?.entries
                            ?.map { user.sex == it.value }
                            ?.all { it } ?: false
                }
    }
}