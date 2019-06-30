package zhi.yest.communityintersection.peopleservice.domain

data class Request(val groupIds: List<String>,
                   val fields: Map<String, String>)
