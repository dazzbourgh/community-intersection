package zhi.yest.communityintersection.peopleservice.service.vk

interface DelayingRequestSender {
    suspend fun <T> request(block: suspend () -> T): T
}
