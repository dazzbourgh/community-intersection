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
        val interestingUsers = mutableListOf<User>()
        val usersMap = mutableMapOf<User, Int>()
        request.communities
                .asSequence()
                // using distinct since new users may subscribe while batch searching
                .map { groupService.getMembers(it).distinct() }
                .forEach { userList ->
                    userList.forEach { user ->
                        usersMap.computeIfPresent(user) { _, value ->
                            (value + 1).also { if (it == request.communities.size) interestingUsers.add(user) }
                        } ?: usersMap.put(user, 1)
                    }
                }
        return interestingUsers
                .filter { user ->
                    request.peopleFilters
                            ?.entries
                            ?.map { user.fields[it.key] == it.value }
                            ?.all { it } ?: true
                }
    }
}