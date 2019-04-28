package zhi.yest.vk.friendfinder.vk

import kotlinx.coroutines.Deferred

interface DelayedRequestSender<T> {
    suspend fun requestAsync(block: () -> T): Deferred<T>
}
