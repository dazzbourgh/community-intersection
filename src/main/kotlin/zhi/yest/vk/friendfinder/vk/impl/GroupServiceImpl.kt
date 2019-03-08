package zhi.yest.vk.friendfinder.vk.impl

import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import zhi.yest.vk.friendfinder.domain.FIELDS
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto
import zhi.yest.vk.friendfinder.util.trimQuotes
import zhi.yest.vk.friendfinder.vk.GroupService
import zhi.yest.vkmethodexecutor.Methods
import zhi.yest.vkmethodexecutor.VkMethodExecutor

private const val THRESHOLD = 1000

@Service
class GroupServiceImpl(private val vkMethodExecutor: VkMethodExecutor) : GroupService {
    override fun getMembers(groupIds: List<Int>): Flux<DownloadableDataDto<User>> {
        return getMembersCount(groupIds)
                .map { groupIds.zip(it).toMap() }
                .map { getMembersIteratively(it) }
                .toFlux()
                .flatMap { it }
    }

    private fun getMembersCount(groupIds: List<Int>): Mono<List<Int>> {
        return groupIds
                .map { getMembersCount(it).toFlux() }
                .reduce { acc, mono -> acc.concatWith(mono) }
                .collectList()
    }

    private fun getMembersIteratively(idToUserCountMap: Map<Int, Int>): Flux<DownloadableDataDto<User>> {
        val largestUserCount = idToUserCountMap.values.max()!!
        val iterations = largestUserCount / THRESHOLD
        return ((0 until iterations)
                .map { iteration ->
                    getUsersForEachCommunity(idToUserCountMap, iteration * THRESHOLD, largestUserCount)
                } + getUsersForEachCommunity(idToUserCountMap, iterations * THRESHOLD, largestUserCount))
                .reduce { acc, flux -> acc.concatWith(flux) }
                .flatMapIterable { toUserDtoIterable(it) }
    }

    private fun getUsersForEachCommunity(idToUserCountMap: Map<Int, Int>, alreadyFetched: Int, largestUserCount: Int): Flux<DownloadableDataDto<ObjectNode>> {
        return (0 until idToUserCountMap.size)
                .asSequence()
                .map { idToUserCountMap.keys.toList()[it] }
                .filter { idToUserCountMap[it]!! > alreadyFetched }
                .map { getUserJsons(it, THRESHOLD, alreadyFetched) }
                .map { it.toFlux() }
                .reduce { a, b -> a.concatWith(b) }
                .map { DownloadableDataDto(it, ((alreadyFetched.toDouble() / largestUserCount) * 100).toInt()) }
    }

    private fun toUserDtoIterable(objectNode: DownloadableDataDto<ObjectNode>): Iterable<DownloadableDataDto<User>> {
        return objectNode.data!!["response"]["items"]
                .asSequence()
                .map { value ->
                    val user = DownloadableDataDto(User(value["id"].asInt()), objectNode.percent)
                    FIELDS.forEach { field ->
                        value[field]
                                // a trick for 'city' field, which is an object instead of a string
                                ?.let { it["title"] ?: it }
                                ?.toString()
                                ?.also { user.data!!.fields[field] = it.trimQuotes() }
                    }
                    user
                }.asIterable()
    }

    private fun getMembersCount(id: Int): Mono<Int> {
        return vkMethodExecutor.execute(Methods.Groups.GET_BY_ID.toString(),
                mapOf("group_ids" to id.toString(),
                        "fields" to "members_count"))
                .map { it["response"][0]["members_count"].asInt() }
    }

    private fun getUserJsons(groupId: Int, count: Int, offset: Int = 0): Mono<ObjectNode> {
        return vkMethodExecutor.execute(Methods.Groups.GET_MEMBERS.toString(),
                mapOf(
                        "count" to count.toString(),
                        "offset" to (offset).toString(),
                        "group_id" to groupId.toString(),
                        "fields" to FIELDS.filter { it != "first_name" && it != "last_name" }.joinToString()
                ))
    }
}