package zhi.yest.vk.communityscanner.domain

data class User(val id: Int,
                val fields: MutableMap<String, String> = mutableMapOf("link" to """https://vk.com/id$id""")) {
    /*
                val name: String,
                val surname: String,
                val sex: String,
                val photo: String,
                val city: String,
     */
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
        "city")
