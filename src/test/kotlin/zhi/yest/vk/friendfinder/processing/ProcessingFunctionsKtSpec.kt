package zhi.yest.vk.friendfinder.processing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto

class ProcessingFunctionsKtSpec {

    private val users = listOf(DownloadableDataDto(User(0), 5))
    @ExperimentalCoroutinesApi
    private val producerScope = GlobalScope.produce {
        send(0)
    }

    fun shouldFindInteresting() = runBlocking {

    }

    fun filterByFields() {
    }

    fun filterFaceless() {
    }
}
