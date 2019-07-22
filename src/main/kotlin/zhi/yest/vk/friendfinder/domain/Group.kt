package zhi.yest.vk.friendfinder.domain

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class Group(val id: Int,
                 @JsonAlias("name")
                 @JsonProperty("name")
                 val name: String,
                 @JsonAlias("screen_name")
                 @JsonProperty("screen_name")
                 val screenName: String,
                 @JsonAlias("is_closed")
                 @JsonProperty("is_closed")
                 val closed: Boolean,
                 @JsonAlias("type")
                 @JsonProperty("type")
                 val type: String,
                 @JsonAlias("is_admin")
                 @JsonProperty("is_admin")
                 val admin: Boolean,
                 @JsonAlias("admin_level")
                 @JsonProperty("admin_level")
                 val adminLevel: Boolean,
                 @JsonAlias("is_member")
                 @JsonProperty("is_member")
                 val member: Boolean,
                 val description: String?,
                 @JsonAlias("photo_50")
                 @JsonProperty("photo_50")
                 val photo50: String?,
                 @JsonAlias("photo_100")
                 @JsonProperty("photo_100")
                 val photo100: String?,
                 @JsonAlias("photo_200")
                 @JsonProperty("photo_200")
                 val photo200: String?,
                 val link: String = """https://vk.com/club$id""") {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}