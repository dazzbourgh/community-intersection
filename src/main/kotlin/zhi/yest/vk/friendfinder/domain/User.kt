package zhi.yest.vk.friendfinder.domain

data class User(val id: Int,
                val fields: MutableMap<String, String> = mutableMapOf("link" to """https://vk.com/id$id""")) {
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

val FIELDS: List<String> = listOf("first_name",
        "last_name",
        "sex",
        "photo_400_orig",
        "photo_200",
        "city")
