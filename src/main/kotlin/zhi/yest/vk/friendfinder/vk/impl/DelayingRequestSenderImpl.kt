package zhi.yest.vk.friendfinder.vk.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import zhi.yest.vk.friendfinder.vk.DelayingRequestSender
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
@ObsoleteCoroutinesApi
class DelayingRequestSenderImpl(@Value("\${vk.request.interval}") private val interval: Int) : DelayingRequestSender {
    private var lastRequestTime: LocalDateTime = LocalDateTime.now()
    //SupervisorJob is used because we want to have continuous processing of requestChannel
    //even if one of the requests fails
    private val coroutineScope = CoroutineScope(SupervisorJob())
    private val actor = coroutineScope.actor<Deferred<*>> {
        for (request in channel) {
            val now = LocalDateTime.now()
            val diff = ChronoUnit.MILLIS.between(lastRequestTime, now)
            if (diff < interval) {
                delay(interval - diff)
            }
            lastRequestTime = LocalDateTime.now()
            request.start()
        }
    }

    override suspend fun <T> request(block: () -> T): T {
        val deferred = coroutineScope.async(start = CoroutineStart.LAZY) { block() }
        actor.send(deferred)
        return deferred.await()
    }
}
