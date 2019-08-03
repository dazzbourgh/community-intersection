package zhi.yest.vk.friendfinder.vk

import zhi.yest.vk.friendfinder.domain.User

interface UserService {
    suspend fun search(groupId: String, fields: Map<String, String>): List<User>
}
