package zhi.yest.vk.friendfinder.vk

interface DelayingRequestSender {
    suspend fun <T> request(block: () -> T): T
}
