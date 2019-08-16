package zhi.yest.communityintersection.peopleservice.vk

import zhi.yest.communityintersection.peopleservice.domain.User

interface UserService {
    suspend fun search(groupId: String, fields: Map<String, String>, token: String): List<User>
}
