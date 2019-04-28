package zhi.yest.vk.friendfinder.controller.people

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.Flux
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.vk.GroupService

@ExperimentalCoroutinesApi
object PeopleFunctionsSpec : Spek({
    describe("People's functions") {
        describe("user fetching function") {
            it("should get users and send them to channel alongside percentage DTOs") {
                val groupService = mock<GroupService> {
                    on { runBlocking { getMembersCount(any()) } } doReturn 1
                    on { runBlocking { getMembers(any(), any()) } } doReturn Flux.just(User(1))
                }
                val request = Request(listOf(1))
                runBlocking {
                    fetchUsers(groupService).also {
                        //TODO
                    }
                }
            }
        }
    }
})
