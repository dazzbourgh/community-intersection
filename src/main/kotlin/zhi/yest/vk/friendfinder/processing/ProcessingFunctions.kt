package zhi.yest.vk.friendfinder.processing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto
import zhi.yest.vk.friendfinder.util.trimQuotes
import java.util.concurrent.ConcurrentHashMap

@ExperimentalCoroutinesApi
suspend fun ProducerScope<DownloadableDataDto<out User>>.processUsers(userChannel: Channel<DownloadableDataDto<out User>>, request: Request) {
    val interestingUsers = filterInteresting(userChannel, request.communities.size)
    val matchingUsers = filterByFields(interestingUsers) { request.peopleFilters }
    val usersWithPhoto = filterPhotos(matchingUsers)
    for (userDto in usersWithPhoto) send(userDto)
}

@ExperimentalCoroutinesApi
fun ProducerScope<DownloadableDataDto<out User>>.filterInteresting(input: ReceiveChannel<DownloadableDataDto<out User>>,
                                                                   communitiesCount: Int,
                                                                   userCountMap: MutableMap<User, Int> = ConcurrentHashMap()) = createProducer(input) {
    sendIfInteresting(it, communitiesCount, userCountMap)
}

@ExperimentalCoroutinesApi
fun ProducerScope<DownloadableDataDto<out User>>.filterByFields(input: ReceiveChannel<DownloadableDataDto<out User>>,
                                                                filterMapSupplier: () -> Map<String, String>?) = createProducer(input) {
    sendIfMatches(it, filterMapSupplier)
}

@ExperimentalCoroutinesApi
fun ProducerScope<DownloadableDataDto<out User>>.filterPhotos(input: ReceiveChannel<DownloadableDataDto<out User>>) = createProducer(input) {
    if (it.data!!.fields["photo_200"] != null && it.data.fields["photo_200"] != "") send(it)
}

@ExperimentalCoroutinesApi
suspend fun ProducerScope<DownloadableDataDto<out User>>.sendIfInteresting(userDto: DownloadableDataDto<out User>,
                                                                           communitiesCount: Int,
                                                                           userCountMap: MutableMap<User, Int>) {
    if (userCountMap.compute(userDto.data!!) { _, value ->
                if (value == null) 1 else value + 1
            } == communitiesCount) send(userDto)
}

@ExperimentalCoroutinesApi
suspend fun ProducerScope<DownloadableDataDto<out User>>.sendIfMatches(userDto: DownloadableDataDto<out User>, filterMapSupplier: () -> Map<String, String>?) {
    val filterMap = filterMapSupplier() ?: emptyMap()
    if (filterMap.isEmpty() || filterMap
                    .entries
                    .map { userDto.data!!.fields[it.key] == it.value.trimQuotes() }
                    .all { it }) send(userDto)
}

@ExperimentalCoroutinesApi
fun ProducerScope<DownloadableDataDto<out User>>.createProducer(input: ReceiveChannel<DownloadableDataDto<out User>>,
                                                                action: suspend (DownloadableDataDto<out User>) -> Unit) = produce {
    for (userDto in input) {
        val user = userDto.data
        // null user means this is a percentage only DTO, no processing
        // is required for such objects and we just push it further
        // down the pipeline
        if (user == null) send(userDto)
        action(userDto)
    }
}
