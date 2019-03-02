package zhi.yest.vk.communityscanner.vk

import reactor.core.publisher.Flux
import zhi.yest.vk.communityscanner.domain.User
import zhi.yest.vk.communityscanner.dto.DownloadableDataDto

interface GroupService {
    fun getMembers(groupIds: List<Int>): Flux<DownloadableDataDto<User>>
}