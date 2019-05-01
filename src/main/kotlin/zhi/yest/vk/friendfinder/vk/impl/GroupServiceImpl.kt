package zhi.yest.vk.friendfinder.vk.impl

import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import zhi.yest.vk.friendfinder.domain.FIELDS
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.util.trimQuotes
import zhi.yest.vk.friendfinder.vk.DelayingSupplier
import zhi.yest.vk.friendfinder.vk.GroupService
import zhi.yest.vkmethodexecutor.Methods
import zhi.yest.vkmethodexecutor.VkMethodExecutor

@Service
@ConfigurationProperties(prefix = "vk.group")
class GroupServiceImpl(private val delayingSupplier: DelayingSupplier,
                       private val vkMethodExecutor: VkMethodExecutor) : GroupService {

    @Value("\${loops}")
    private var safeLoopsNumber = 0
    @Value("\${threshold}")
    private var threshold = 0
    @Value("\${maxUsers}")
    private var maxUsersPerIteration = 0

    override suspend fun getMembersCount(groupId: Int): Int =
            delayingSupplier.supply {
                vkMethodExecutor.execute(Methods.Groups.GET_BY_ID.toString(),
                        mapOf("group_ids" to groupId.toString(),
                                "fields" to "members_count"))
            }
                    .map { it["response"][0]["members_count"].asInt() }
                    .awaitSingle()


    override suspend fun getMembers(groupId: Int, membersCount: Int): Flux<User> {
        val requestsNumber = membersCount / maxUsersPerIteration + if (membersCount % maxUsersPerIteration > 0) 1 else 0

        // TODO: possible security breach as user input is inserted, requires validation
        return (0 until requestsNumber)
                .map { i ->
                    val remainder = membersCount - maxUsersPerIteration * i
                    val loops = if (remainder > safeLoopsNumber) safeLoopsNumber else remainder / threshold + 1
                    delayingSupplier.supply {
                        vkMethodExecutor.execute(Methods.Execute.EXECUTE.code("getUsers"),
                                mapOf("start" to "${i * safeLoopsNumber}",
                                        "loops" to "$loops",
                                        "groupId" to "$groupId",
                                        "threshold" to "$threshold"))
                    }
                }
                .map { it.toFlux() }
                .reduce { acc, mono -> acc.concatWith(mono) }
                .map { it.toUserList() }
                .flatMapIterable { it }
    }
}

private fun ObjectNode.toUserList(): List<User> {
    return this["response"]
            .toList()
            .flatten()
            .map { value ->
                FIELDS
                        .map { field ->
                            field to (value[field]
                                    // a trick for 'city' field, which is an object instead of a string
                                    ?.let { it["title"] ?: it }
                                    ?.toString()
                                    ?.let { it.trimQuotes() } ?: "")
                        }
                        .toMap()
                        .let { User(value["id"].asInt(), it) }
            }
}
