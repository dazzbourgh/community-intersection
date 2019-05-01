package zhi.yest.vk.friendfinder.vk.impl

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import zhi.yest.vk.friendfinder.vk.DelayingSupplier
import kotlin.system.measureTimeMillis

object DelayingSupplierImplSpec : Spek({
    val interval = 50
    val items = 20
    val delayingSupplier: DelayingSupplier = DelayingSupplierImpl(interval)
    describe("delaying supplier") {
        it("should supply with interval") {
            var time = 0L
            var result: List<Int>? = null
            runBlocking {
                (delayingSupplier as DelayingSupplierImpl).startProcessing()
                time = measureTimeMillis {
                    val deferreds = mutableListOf<Deferred<Int>>()
                    repeat(items) {
                        async {
                            delayingSupplier.supply { it }
                        }
                                .also { deferreds.add(it) }
                                .also { result = deferreds.map { it.await() } }
                    }
                }
            }
            assertTrue(time > interval * items - 2 * interval)
            assertIterableEquals((0 until items), result)
        }
    }
})
