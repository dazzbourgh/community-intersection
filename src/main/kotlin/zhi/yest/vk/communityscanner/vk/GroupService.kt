package zhi.yest.vk.communityscanner.vk

import reactor.core.publisher.Flux
import zhi.yest.vk.communityscanner.domain.User

interface GroupService {
    fun getMembers(groupIds: List<Int>): Flux<User>
}