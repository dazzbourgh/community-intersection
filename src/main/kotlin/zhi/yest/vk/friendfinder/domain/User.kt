package zhi.yest.vk.friendfinder.domain

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class User(val id: Int,
                @JsonAlias("first_name")
                @JsonProperty("first_name")
                val firstName: String,
                @JsonAlias("last_name")
                @JsonProperty("last_name")
                val lastName: String,
                @JsonAlias("sex")
                @JsonProperty("sex")
                val sex: String,
                @JsonAlias("photo_400_orig")
                @JsonProperty("photo_400_orig")
                val photo400: String?,
                @JsonAlias("photo_200")
                @JsonProperty("photo_200")
                val photo200: String?,
                @JsonAlias("city")
                @JsonProperty("city")
                val city: Any?,
                val link: String = """https://vk.com/id$id""") {
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
