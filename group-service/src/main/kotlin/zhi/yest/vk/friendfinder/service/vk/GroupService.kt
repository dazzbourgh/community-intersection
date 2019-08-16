package zhi.yest.vk.friendfinder.service.vk

import zhi.yest.vk.friendfinder.domain.Group

interface GroupService {
    suspend fun findById(groupId: String, token: String): Group
}