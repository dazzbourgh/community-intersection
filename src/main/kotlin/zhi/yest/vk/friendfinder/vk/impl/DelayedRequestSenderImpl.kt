package zhi.yest.vk.friendfinder.vk.impl

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import zhi.yest.vk.friendfinder.vk.DelayedRequestSender
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.annotation.PostConstruct

private const val REQUEST_INTERVAL = 500

@Service
class DelayedRequestSenderImpl<T> : DelayedRequestSender<T> {
    private var lastRequestTime: LocalDateTime = LocalDateTime.now()
    private val requestChannel: Channel<Deferred<T>> = Channel()

    override suspend fun requestAsync(block: () -> T): Deferred<T> {
        val deferred = GlobalScope.async(start = CoroutineStart.LAZY) { block() }
        requestChannel.send(deferred)
        return deferred
    }

    @PostConstruct
    private fun startRequestProcessing() = GlobalScope.launch {
        for (request in requestChannel) {
            val now = LocalDateTime.now()
            val diff = ChronoUnit.MILLIS.between(lastRequestTime, now)
            if (diff < REQUEST_INTERVAL) {
                delay(REQUEST_INTERVAL - diff)
                lastRequestTime = now
            }
            request.start()
        }
    }
}
