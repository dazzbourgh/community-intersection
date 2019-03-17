package zhi.yest.vk.friendfinder.processing

import reactor.core.publisher.Flux
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto
import zhi.yest.vk.friendfinder.util.trimQuotes
import java.util.concurrent.ConcurrentHashMap

fun Flux<DownloadableDataDto<User>>.findInteresting(communitiesCount: Int, userCountMap: MutableMap<User, Int> = ConcurrentHashMap()): Flux<DownloadableDataDto<User>> {
    return this
            .filter {
                userCountMap.compute(it.data!!) { _, value ->
                    if (value == null) 1 else value + 1
                } == communitiesCount
            }
}

fun Flux<DownloadableDataDto<User>>.filterByFields(filterMapSupplier: () -> Map<String, String>?): Flux<DownloadableDataDto<User>> {
    return this
            .map { userDto ->
                val filterMap = filterMapSupplier() ?: emptyMap()
                if (filterMap.isEmpty() || filterMap
                                .entries
                                .map { userDto.data!!.fields[it.key] == it.value.trimQuotes() }
                                .all { it }) userDto else DownloadableDataDto<User>(null, userDto.percent)
            }
}

fun Flux<DownloadableDataDto<User>>.filterFaceless(): Flux<DownloadableDataDto<User>> {
    return this
            .filter { it.data?.fields?.get("photo_200") != null && it.data.fields["photo_200"] != "" }
}

fun Flux<DownloadableDataDto<User>>.addFinisher(): Flux<DownloadableDataDto<User>> {
    return this.concatWith { Flux.just(DownloadableDataDto(null, 100)) }
}