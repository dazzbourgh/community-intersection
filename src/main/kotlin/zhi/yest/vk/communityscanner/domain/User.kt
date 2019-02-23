package zhi.yest.vk.communityscanner.domain

data class User(val id: Int,
                val name: String,
                val surname: String,
                val sex: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}
