package zhi.yest.communityintersection.peopleservice.vk

interface DelayingRequestSender {
    suspend fun <T> request(block: suspend () -> T): T
}
