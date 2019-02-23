package zhi.yest.vk.communityscanner.domain

data class Request(val communities: List<Int>,
                   val peopleFilters: Map<String, String>?)