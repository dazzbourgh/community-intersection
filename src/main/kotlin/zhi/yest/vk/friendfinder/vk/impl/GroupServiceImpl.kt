package zhi.yest.vk.friendfinder.vk.impl

import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import zhi.yest.vk.friendfinder.domain.FIELDS
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.util.trimQuotes
import zhi.yest.vk.friendfinder.vk.DelayingRequestSender
import zhi.yest.vk.friendfinder.vk.GroupService
import zhi.yest.vkmethodexecutor.Methods
import zhi.yest.vkmethodexecutor.VkMethodExecutor
import java.util.logging.Level
import java.util.logging.Logger

val LOGGER = Logger.getLogger(GroupServiceImpl::class.java.simpleName)

@Service
class GroupServiceImpl(private val delayingRequestSender: DelayingRequestSender,
                       private val vkMethodExecutor: VkMethodExecutor,
                       config: GroupServiceConfig) : GroupService {
    private val safeLoopsNumber: Int = config.props["loops"]
            ?: error("Safe loops number was not provided in properties.")
    private val threshold: Int = config.props["threshold"] ?: error("Threshold was not provided in properties.")
    private val maxUsersPerIteration: Int

    init {
        maxUsersPerIteration = safeLoopsNumber * threshold
    }

    override suspend fun getMembersCount(groupId: Int): Int =
            delayingRequestSender.request {
                vkMethodExecutor.execute(Methods.Groups.GET_BY_ID.toString(),
                        mapOf("group_ids" to groupId.toString(),
                                "fields" to "members_count"))
            }
                    .map { it.toMembersCount() }
                    .awaitSingle()


    override suspend fun getMembers(groupId: Int, membersCount: Int): Flux<User> {
        val requestsNumber = membersCount / maxUsersPerIteration + if (membersCount % maxUsersPerIteration > 0) 1 else 0

        // TODO: possible security breach as user input is inserted, requires validation
        return (0 until requestsNumber)
                .map { i ->
                    val remainder = membersCount - maxUsersPerIteration * i
                    val loops = if (remainder > safeLoopsNumber) safeLoopsNumber else remainder / threshold + 1
                    delayingRequestSender.request {
                        vkMethodExecutor.execute(Methods.Execute.EXECUTE.code("getUsers"),
                                mapOf("start" to "${i * safeLoopsNumber}",
                                        "loops" to "$loops",
                                        "groupId" to "$groupId",
                                        "threshold" to "$threshold"))
                    }
                }
                .map { it.toFlux() }
                .reduce { acc, mono ->
                    acc.concatWith(mono
                            .onErrorResume { err ->
                                LOGGER.log(Level.SEVERE, err) { "Error during request to VK" }
                                Mono.empty()
                            })
                }
                .map { it.toUserList() }
                .flatMapIterable { it }
    }
}

private fun ObjectNode.toMembersCount(): Int {
    return this["response"][0]["members_count"].asInt()
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

@Configuration("groupServiceConfig")
@ConfigurationProperties(prefix = "vk.group")
@EnableConfigurationProperties
class GroupServiceConfig {
    lateinit var props: Map<String, Int>
}
