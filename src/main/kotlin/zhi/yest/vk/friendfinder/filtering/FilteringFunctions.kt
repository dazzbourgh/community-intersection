package zhi.yest.vk.friendfinder.filtering

import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.util.trimQuotes

fun filterInteresting(userCountMap: MutableMap<User, Int>): (User) -> (Request) -> Boolean = { user ->
    { request ->
        userCountMap.compute(user) { _, value ->
            if (value == null) 1 else value + 1
        } == request.communities.size
    }
}

fun filterByFields(user: User): (Request) -> Boolean = { request ->
    val filterMap = request.peopleFilters
    filterMap == null || filterMap.isEmpty() || filterMap.entries
            .map { user.fields[it.key] == it.value.trimQuotes() }
            .all { it }
}

fun filterPhotos(user: User): (Request) -> Boolean = { _ ->
    user.fields["photo_200"] != null && user.fields["photo_200"] != ""
}
