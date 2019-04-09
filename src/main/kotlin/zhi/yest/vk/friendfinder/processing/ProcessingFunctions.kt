package zhi.yest.vk.friendfinder.processing

import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.util.trimQuotes
import java.util.concurrent.ConcurrentHashMap

fun Sequence<User>.findInteresting(communitiesCount: Int, userCountMap: MutableMap<User, Int> = ConcurrentHashMap()): Sequence<User> {
    return this
            .filter {
                userCountMap.compute(it) { _, value ->
                    if (value == null) 1 else value + 1
                } == communitiesCount
            }
}

fun Sequence<User>.filterByFields(filterMapSupplier: () -> Map<String, String>?): Sequence<User> {
    return this
            .filter { user ->
                val filterMap = filterMapSupplier() ?: emptyMap()
                filterMap.isEmpty() || filterMap
                        .entries
                        .map { user.fields[it.key] == it.value.trimQuotes() }
                        .all { it }
            }
}

fun Sequence<User>.filterFaceless(): Sequence<User> {
    return this
            .filter { it.fields["photo_200"] != null && it.fields["photo_200"] != "" }
}
