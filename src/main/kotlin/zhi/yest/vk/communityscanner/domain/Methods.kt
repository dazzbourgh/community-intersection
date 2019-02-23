package zhi.yest.vk.communityscanner.domain

class Methods {
    enum class Groups(private val methodName: String) {
        GET_BY_ID("getById"),
        GET_MEMBERS("getMembers");

        override fun toString(): String {
            return """groups.$methodName"""
        }
    }

    enum class Users(private val methodName: String) {
        SEARCH("search"),
        GET("get");

        override fun toString(): String {
            return """users.$methodName"""
        }
    }
}

