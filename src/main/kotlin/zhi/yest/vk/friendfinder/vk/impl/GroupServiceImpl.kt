package zhi.yest.vk.friendfinder.vk.impl

import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import zhi.yest.vk.friendfinder.domain.FIELDS
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.util.trimQuotes
import zhi.yest.vk.friendfinder.vk.GroupService
import zhi.yest.vkmethodexecutor.Methods
import zhi.yest.vkmethodexecutor.VkMethodExecutor

const val SAFE_LOOPS_NUMBER = 25
const val THRESHOLD = 1000
const val MAX_USERS_PER_ITERATION = SAFE_LOOPS_NUMBER * THRESHOLD

@Service
class GroupServiceImpl(private val vkMethodExecutor: VkMethodExecutor) : GroupService {
    override suspend fun getMembersCount(groupId: Int): Int {
        return vkMethodExecutor.execute(Methods.Groups.GET_BY_ID.toString(),
                mapOf("group_ids" to groupId.toString(),
                        "fields" to "members_count"))
                .map { it["response"][0]["members_count"].asInt() }
                .awaitSingle()
    }

    override suspend fun getMembers(groupId: Int, membersCount: Int): Flux<User> {
        val requestsNumber = membersCount / MAX_USERS_PER_ITERATION + if (membersCount % MAX_USERS_PER_ITERATION > 0) 1 else 0

        // TODO: possible security breach as user input is inserted, requires validation
        return (0 until requestsNumber)
                .map { i ->
                    val remainder = membersCount - MAX_USERS_PER_ITERATION * i
                    val loops = if (remainder > SAFE_LOOPS_NUMBER) SAFE_LOOPS_NUMBER else remainder / THRESHOLD + 1
                    GlobalScope.async {
                        delay(i * 500L)
                        vkMethodExecutor.execute(Methods.Execute.EXECUTE.code("getUsers"),
                                mapOf("start" to "${i * SAFE_LOOPS_NUMBER}",
                                        "loops" to "$loops",
                                        "groupId" to "$groupId",
                                        "threshold" to "$THRESHOLD"))
                    }
                }
                .map { it.await() }
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
