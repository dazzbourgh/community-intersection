package zhi.yest.vk.friendfinder.vk

import reactor.core.publisher.Flux
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.dto.DownloadableDataDto

interface GroupService {
    fun getMembers(groupIds: List<Int>): Flux<DownloadableDataDto<User>>
}