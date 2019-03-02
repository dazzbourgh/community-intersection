package zhi.yest.vk.communityscanner.processing

import reactor.core.publisher.Flux
import zhi.yest.vk.communityscanner.domain.User
import java.util.concurrent.ConcurrentHashMap

fun Flux<User>.findInteresting(communitiesCount: Int, userCountMap: MutableMap<User, Int> = ConcurrentHashMap()): Flux<User> {
    return this
            .filter {
                userCountMap.compute(it) { _, value ->
                    if (value == null) 1 else value + 1
                } == communitiesCount
            }
}

fun Flux<User>.filterByFields(filterMapSupplier: () -> Map<String, String>?): Flux<User> {
    return this
            .filter { user ->
                val filterMap = filterMapSupplier() ?: emptyMap()
                filterMap.isEmpty() || filterMap
                        .entries
                        .map { user.fields[it.key] == it.value }
                        .all { it }
            }
}