package zhi.yest.vk.friendfinder.domain

data class Request(val communities: List<Int>,
                   val peopleFilters: Map<String, String>? = null)
