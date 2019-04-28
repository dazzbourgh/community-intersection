package zhi.yest.vk.friendfinder.filtering

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.domain.User

object FilteringFunctionsSpec : Spek({
    describe("Processing functions") {
        val communitiesCount = 2
        val interestingUser = User(0, mapOf("photo_200" to "pic", "city" to "Moscow"))
        val boringUser = User(1, mapOf("city" to "Los Angeles"))
        val request = Request(listOf(1, 2), mapOf("city" to "Los Angeles"))

        describe("a function filtering interesting users") {
            val userCountMap = mutableMapOf(interestingUser to communitiesCount - 1)
            it("filters out boring users") {
                assertTrue(filterInteresting(userCountMap)(interestingUser)(request))
                assertFalse(filterInteresting(userCountMap)(boringUser)(request))
            }
        }

        describe("a function filtering users that match filter map") {
            it("sends every user if no filter map is provided") {
                val requestWithoutFilters = Request(listOf(1))
                assertTrue(filterByFields(interestingUser)(requestWithoutFilters))
                assertTrue(filterByFields(boringUser)(requestWithoutFilters))
            }
            it("sends only users that match filter map") {
                assertTrue(filterByFields(boringUser)(request))
                assertFalse(filterByFields(interestingUser)(request))
            }
        }

        describe("a function that filters users with photos") {
            it("sends only users with photos") {
                assertTrue(filterPhotos(interestingUser)(request))
                assertFalse(filterPhotos(boringUser)(request))
            }
        }
    }
})
