package zhi.yest.vk.friendfinder.service.vk

interface DelayingRequestSender {
    suspend fun <T> request(block: suspend () -> T): T
}
