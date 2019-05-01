package zhi.yest.vk.friendfinder.vk.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import zhi.yest.vk.friendfinder.vk.DelayingSupplier
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.annotation.PostConstruct
import kotlin.coroutines.EmptyCoroutineContext

@Service
class DelayingSupplierImpl(@Value("\${vk.request.interval}") private val interval: Int) : DelayingSupplier {
    private var lastRequestTime: LocalDateTime = LocalDateTime.now()
    private val requestChannel: Channel<Deferred<*>> = Channel()
    private val coroutineScope = CoroutineScope(EmptyCoroutineContext)

    override suspend fun <T> supply(block: () -> T): T {
        val deferred = coroutineScope.async(start = CoroutineStart.LAZY) { block() }
        requestChannel.send(deferred)
        return deferred.await()
    }

    @PostConstruct
    fun startProcessing() = coroutineScope.launch(context = Job(coroutineScope.coroutineContext[Job])) {
        for (request in requestChannel) {
            val now = LocalDateTime.now()
            val diff = ChronoUnit.MILLIS.between(lastRequestTime, now)
            if (diff < interval) {
                delay(interval - diff)
            }
            lastRequestTime = LocalDateTime.now()
            request.start()
        }
    }
}
