package zhi.yest.vk.friendfinder.vk

interface DelayingRequestSender {
    suspend fun <T> request(block: suspend () -> T): T
}
