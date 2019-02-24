package zhi.yest.vk.communityscanner.vk.impl

import org.springframework.stereotype.Service
import zhi.yest.vk.communityscanner.domain.FIELDS
import zhi.yest.vk.communityscanner.domain.Methods
import zhi.yest.vk.communityscanner.domain.User
import zhi.yest.vk.communityscanner.vk.GroupService
import zhi.yest.vk.communityscanner.vk.VkMethodExecutor

private const val THRESHOLD = 1000

@Service
class GroupServiceImpl(private val vkMethodExecutor: VkMethodExecutor) : GroupService {
    override fun getMembers(groupId: Int): List<User> {
        val membersCount = getMembersCount(groupId)
        val loops = membersCount / THRESHOLD
        val remainder = if (membersCount > THRESHOLD) membersCount % THRESHOLD else 0
        return if (loops > 0) ((0 until loops)
                .asSequence()
                .flatMap { getUsers(groupId, THRESHOLD, it) } + getUsers(groupId, remainder, loops))
                .toList()
        else getUsers(groupId, THRESHOLD).toList()
    }

    private fun getMembersCount(id: Int): Int {
        return vkMethodExecutor.execute(Methods.Groups.GET_BY_ID.toString(),
                mapOf("group_ids" to id.toString(),
                        "fields" to "members_count"))["response"][0]["members_count"]
                .asInt()
    }

    private fun getUsers(groupId: Int, count: Int, offset: Int = 0): Sequence<User> {
        if (count == 0) return emptySequence()
        return vkMethodExecutor.execute(Methods.Groups.GET_MEMBERS.toString(),
                mapOf(
                        "count" to count.toString(),
                        "offset" to (offset * THRESHOLD).toString(),
                        "group_id" to groupId.toString(),
                        "fields" to FIELDS.filter { it != "first_name" && it != "last_name" }.joinToString()
                ))["response"]["items"]
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
                }
    }
}