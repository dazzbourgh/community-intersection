package zhi.yest.vk.friendfinder.domain

data class Request(val groupIds: List<String>,
                   val fields: Map<String, String>)
