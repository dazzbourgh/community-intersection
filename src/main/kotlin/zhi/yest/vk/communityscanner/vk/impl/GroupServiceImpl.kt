package zhi.yest.vk.communityscanner.vk.impl

import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import zhi.yest.vk.communityscanner.domain.FIELDS
import zhi.yest.vk.communityscanner.domain.Methods
import zhi.yest.vk.communityscanner.domain.User
import zhi.yest.vk.communityscanner.vk.GroupService
import zhi.yest.vk.communityscanner.vk.VkMethodExecutor

private const val THRESHOLD = 1000

@Service
class GroupServiceImpl(private val vkMethodExecutor: VkMethodExecutor) : GroupService {
    override fun getMembers(groupId: Int): Flux<User> {
        val membersCount = 121 //getMembersCount(groupId)
        val loops = membersCount / THRESHOLD
        val remainder = if (membersCount > THRESHOLD) membersCount % THRESHOLD else 0
        val userJsonsFlux = when {
            loops > 0 -> (0 until loops)
                    .asSequence()
                    .map { getUserJsons(groupId, THRESHOLD, it) }
                    .map { it.toFlux() }
                    .reduce { acc, flux -> acc.concatWith(flux) }
                    .let { it.concatWith(getUserJsons(groupId, remainder, loops)) }
            else -> getUserJsons(groupId, THRESHOLD)
        }.toFlux()
        return userJsonsFlux
                .flatMapIterable { objectNode ->
                    objectNode["response"]["items"]
                            .asSequence()
                            .map { value ->
                                val user = User(value["id"].asInt())
                                FIELDS.forEach { field ->
                                    value[field]
                                            ?.let { it["title"] ?: it }
                                            ?.toString()
                                            ?.also { user.fields[field] = it }

                                }
                                user
                            }.asIterable()
                }
    }

    private fun getMembersCount(id: Int): Int {
        return vkMethodExecutor.execute(Methods.Groups.GET_BY_ID.toString(),
                mapOf("group_ids" to id.toString(),
                        "fields" to "members_count"))
                .block()!!["response"][0]["members_count"]
                .asInt()
    }

    private fun getUserJsons(groupId: Int, count: Int, offset: Int = 0): Mono<ObjectNode> {
        if (count == 0) return Mono.empty()
        return vkMethodExecutor.execute(Methods.Groups.GET_MEMBERS.toString(),
                mapOf(
                        "count" to count.toString(),
                        "offset" to (offset * THRESHOLD).toString(),
                        "group_id" to groupId.toString(),
                        "fields" to "sex,photo_400_orig,city"
                ))
    }
}