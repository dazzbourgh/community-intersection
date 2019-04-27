package zhi.yest.vk.friendfinder.processing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto

@ExperimentalCoroutinesApi
object ProcessingFunctionsSpec : Spek({
    describe("Processing functions") {
        val communitiesCount = 2
        val interestingUser = DownloadableDataDto(
                User(0, mapOf("photo_200" to "pic", "city" to "Moscow")),
                0)
        val boringUser = DownloadableDataDto(User(1, mapOf("city" to "Los Angeles")), 10)
        val users = listOf(interestingUser, boringUser)
        var totalUsersSent = 0

        beforeEachTest { totalUsersSent = 0 }

        describe("a function filtering interesting users") {
            val userCountMap = mutableMapOf(interestingUser.data!! to communitiesCount - 1)
            it("sends an interesting user to the channel") {
                runBlocking {
                    GlobalScope.produce<DownloadableDataDto<out User>> {
                        users.forEach { sendIfInteresting(it, communitiesCount, userCountMap) }
                    }.also {
                        for (user in it) {
                            totalUsersSent++
                            assertEquals(interestingUser, user)
                        }
                    }
                }
                assertEquals(totalUsersSent, 1)
            }
        }

        describe("a function filtering users that match filter map") {
            it("sends every user if no filter map is provided") {
                runBlocking {
                    GlobalScope.produce<DownloadableDataDto<out User>> {
                        users.forEach { sendIfMatches(it) { null } }
                    }.also { for (user in it) totalUsersSent++ }
                    assertEquals(totalUsersSent, 2)
                }
            }
            it("sends only users that match filter map") {
                runBlocking {
                    GlobalScope.produce<DownloadableDataDto<out User>> {
                        users.forEach { sendIfMatches(it) { mapOf("city" to "Los Angeles") } }
                    }.also {
                        for (user in it) {
                            totalUsersSent++
                            assertEquals(user, boringUser)
                        }
                        assertEquals(totalUsersSent, 1)
                    }
                }
            }
        }

        describe("a function that filters users with photos") {
            it("sends only users with photos") {
                runBlocking {
                    val channel = Channel<DownloadableDataDto<out User>>()
                    launch {
                        channel.send(interestingUser)
                        channel.send(boringUser)
                        channel.close()
                    }
                    GlobalScope.produce<DownloadableDataDto<out User>> {
                        filterPhotos(channel)
                    }.also {
                        for (user in it) {
                            totalUsersSent++
                            assertEquals(user, interestingUser)
                        }
                        assertEquals(totalUsersSent, 1)
                    }
                }
            }
        }
    }
})
