package zhi.yest.vk.communityscanner.vk

import zhi.yest.vk.communityscanner.domain.User

interface GroupService {
    fun getMembers(groupId: Int): List<User>
}