package zhi.yest.vk.friendfinder.vk

import reactor.core.publisher.Flux
import zhi.yest.vk.friendfinder.domain.User

interface GroupService {
    suspend fun getMembersCount(groupId: Int): Int
    suspend fun getMembers(groupId: Int, membersCount: Int): Flux<User>
}
