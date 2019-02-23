package zhi.yest.vk.communityscanner.vk

import com.fasterxml.jackson.databind.node.ObjectNode

interface VkMethodExecutor {
    fun execute(methodName: String, params: Map<String, String>): ObjectNode
}