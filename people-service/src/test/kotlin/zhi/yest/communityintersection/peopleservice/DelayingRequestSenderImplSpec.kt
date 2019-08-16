package zhi.yest.communityintersection.peopleservice

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import zhi.yest.communityintersection.peopleservice.service.vk.DelayingRequestSender
import zhi.yest.communityintersection.peopleservice.service.vk.impl.DelayingRequestSenderImpl
import kotlin.system.measureTimeMillis

@ObsoleteCoroutinesApi
object DelayingRequestSenderImplSpec : Spek({
    val interval = 50
    val items = 20
    val delayingRequestSender: DelayingRequestSender = DelayingRequestSenderImpl(interval)
    describe("delaying supplier") {
        it("should request with interval") {
            var time = 0L
            var result: List<Int>? = null
            runBlocking {
                time = measureTimeMillis {
                    val deferreds = mutableListOf<Deferred<Int>>()
                    repeat(items) {
                        async {
                            delayingRequestSender.request { it }
                        }
                                .also { deferreds.add(it) }
                                .also { result = deferreds.map { it.await() } }
                    }
                }
            }
            assertTrue(time > interval * items - 2 * interval)
            assertIterableEquals((0 until items), result)
        }
        it("should continue working if request fails") {
            var t = 0
            runBlocking {
                withContext(Dispatchers.Default) {
                    try {
                        delayingRequestSender.request { throw RuntimeException() }
                    } catch (e: Exception) {
                    }
                    t = delayingRequestSender.request { 1 }
                }
            }
            assertEquals(1, t)
        }
    }
})
