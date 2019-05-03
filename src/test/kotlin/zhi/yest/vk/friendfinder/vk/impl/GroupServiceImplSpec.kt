package zhi.yest.vk.friendfinder.vk.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.Mono
import zhi.yest.vk.friendfinder.vk.DelayingRequestSender
import zhi.yest.vk.friendfinder.vk.GroupService
import zhi.yest.vkmethodexecutor.Methods
import zhi.yest.vkmethodexecutor.VkMethodExecutor

@ObsoleteCoroutinesApi
object GroupServiceImplSpec : Spek({
    describe("group service") {
        val config = GroupServiceConfig()
        config.props = mapOf("loops" to 1, "threshold" to 1)
        val mapper = ObjectMapper()
        val userCount = mapper.readValue("""
            {
                "response": [{
                    "members_count": 1
                }]
            }
            """, ObjectNode::class.java)
        val users = mapper.readValue("""
            {
                "response": [[{
                    "id": 1,
                    "first_name": "Test",
                    "last_name": "User",
                    "is_closed": true,
                    "can_access_closed": false,
                    "sex": 2,
                    "photo_200": "https://sun9-9.us...zENqKqB2g.jpg?ava=1",
                    "photo_400_orig": "https://sun9-27.u...dzxyDb19U.jpg?ava=1"
                    }]]
            }
        """, ObjectNode::class.java)
        val requestSender: DelayingRequestSender = DelayingRequestSenderImpl(0)
        val mockExecutor = mock<VkMethodExecutor> {
            on {
                execute(eq(Methods.Groups.GET_BY_ID.toString()),
                        eq(mapOf("group_ids" to "123", "fields" to "members_count")))
            } doReturn Mono.just(userCount)
            on {
                execute(eq(Methods.Execute.EXECUTE.code("getUsers")),
                        eq(mapOf("start" to "0",
                                "loops" to (config.props["loops"]!! + 1).toString(),
                                "groupId" to "1",
                                "threshold" to config.props["threshold"].toString())))
            } doReturn Mono.just(users)
        }
        val groupService: GroupService = GroupServiceImpl(requestSender, mockExecutor, config)

        it("should get members count") {
            val result = runBlocking { groupService.getMembersCount(123) }
            assertEquals(1, result)
        }
        it("should get users") {
            val result = runBlocking { groupService.getMembers(1, 1) }.blockFirst()
            assertEquals(result?.id, 1)
            assertEquals(result!!.fields["first_name"], "Test")
        }
    }
})
