package zhi.yest.vk.friendfinder.vk.impl

import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import zhi.yest.vk.friendfinder.domain.FIELDS
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.util.trimQuotes
import zhi.yest.vk.friendfinder.vk.UserService
import zhi.yest.vkmethodexecutor.Methods
import zhi.yest.vkmethodexecutor.VkMethodExecutor

@Service
class UserServiceImpl(private val vkMethodExecutor: VkMethodExecutor) : UserService {
    override suspend fun search(groupId: String, fields: Map<String, String>): List<User> {
        return vkMethodExecutor.execute(Methods.Execute.EXECUTE.code("searchUsers"),
                mapOf("groupId" to groupId,
                        "sex" to fields["sex"]!!,
                        "city" to fields["city"]!!,
                        "ageFrom" to fields["ageFrom"]!!,
                        "ageTo" to fields["ageTo"]!!))
                .map { it.toUserList() }
                .awaitSingle()
    }
}

private fun ObjectNode.toUserList(): List<User> {
    return this["response"]
            .toList()
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
