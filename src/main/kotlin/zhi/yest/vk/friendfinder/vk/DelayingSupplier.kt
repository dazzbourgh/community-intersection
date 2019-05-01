package zhi.yest.vk.friendfinder.vk

interface DelayingSupplier {
    suspend fun <T> supply(block: () -> T): T
}
