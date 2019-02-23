package zhi.yest.vk.communityscanner.vk

import com.fasterxml.jackson.databind.node.ObjectNode
import reactor.core.publisher.Mono

interface VkMethodExecutor {
    fun execute(methodName: String, params: Map<String, String>): Mono<ObjectNode>
}