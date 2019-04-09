package zhi.yest.vk.friendfinder.vk

import zhi.yest.vk.friendfinder.domain.User

interface GroupService {
    suspend fun getMembers(groupId: Int): Sequence<User>
}