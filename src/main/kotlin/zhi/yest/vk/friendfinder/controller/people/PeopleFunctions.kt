package zhi.yest.vk.friendfinder.controller.people

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.reactive.consumeEach
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import reactor.util.function.Tuple2
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto
import zhi.yest.vk.friendfinder.filtering.filterByFields
import zhi.yest.vk.friendfinder.filtering.filterInteresting
import zhi.yest.vk.friendfinder.filtering.filterPhotos
import zhi.yest.vk.friendfinder.vk.GroupService
import java.util.concurrent.ConcurrentHashMap

@Configuration
class PeopleFunctionsConfiguration {
    @Bean
    @Scope("prototype")
    fun filteringFunctions(): List<(User) -> (Request) -> Boolean> {
        return listOf(filterInteresting(ConcurrentHashMap()),
                ::filterByFields,
                ::filterPhotos)
    }

    @Bean
    fun filteringFunctionsSupplier(applicationContext: ApplicationContext): () -> List<(User) -> (Request) -> Boolean> = {
        @Suppress("UNCHECKED_CAST")
        applicationContext.getBean("filteringFunctions") as List<(User) -> (Request) -> Boolean>
    }

    @ExperimentalCoroutinesApi
    @Bean
    fun fetchUsersBean(groupService: GroupService) = fetchUsers(groupService)
}

@ExperimentalCoroutinesApi
fun fetchUsers(groupService: GroupService): suspend ProducerScope<DownloadableDataDto<out User>>.(request: Request) -> ReceiveChannel<DownloadableDataDto<out User>> = { request ->
    val membersCountList = request.communities.map { groupService.getMembersCount(it) }
    val totalMembersCount = membersCountList.sum()
    val communitiesCountPairs = request.communities.zip(membersCountList)
    produce {
        send(DownloadableDataDto(null, 1))
        communitiesCountPairs.map { groupService.getMembers(it.first, it.second) }
                .reduce { acc, flux -> acc.concatWith(flux) }
                .index()
                .consumeEach { insertPercentageDtos(totalMembersCount, it) }
                .also {
                    send(DownloadableDataDto(null, 100))
                    close()
                }
    }
}

@ExperimentalCoroutinesApi
private suspend fun ProducerScope<DownloadableDataDto<out User>>.insertPercentageDtos(totalMembersCount: Int, indexUserPair: Tuple2<Long, User>) {
    val fivePercentStep = totalMembersCount / 20
    val percentage = (indexUserPair.t1 / totalMembersCount.toFloat()) * 100
    val intPercentage = percentage.toInt()
    val userDto = DownloadableDataDto(indexUserPair.t2, intPercentage)
    if (fivePercentStep > 0 && indexUserPair.t1 % fivePercentStep == 0L) send(DownloadableDataDto(null, intPercentage))
    send(userDto)
}
