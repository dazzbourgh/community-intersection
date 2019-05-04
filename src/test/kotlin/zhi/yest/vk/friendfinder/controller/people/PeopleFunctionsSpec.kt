package zhi.yest.vk.friendfinder.controller.people

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.Flux
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto
import zhi.yest.vk.friendfinder.vk.GroupService

@ExperimentalCoroutinesApi
object PeopleFunctionsSpec : Spek({
    describe("People's functions") {
        describe("user fetching function") {
            it("should get users and send them to channel alongside percentage DTOs") {
                val testUser = User(1)
                val groupService = mock<GroupService> {
                    onBlocking { getMembersCount(any()) } doReturn 1
                    onBlocking { getMembers(any(), any()) } doReturn Flux.just(testUser)
                }
                val request = Request(listOf(1))
                runBlocking {
                    val userChannel = produce<DownloadableDataDto<out User>> {
                        for (user in fetchUsers(groupService)(request)) send(user)
                        close()
                    }
                    assertEquals(1, userChannel.receive().percent)
                    assertEquals(testUser, userChannel.receive().data)
                    assertEquals(100, userChannel.receive().percent)
                }
            }
        }
    }
})
