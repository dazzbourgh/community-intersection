package zhi.yest.vk.friendfinder.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class Group(val id: Int,
                 @JsonProperty("name")
                 val name: String,
                 @JsonProperty("screen_name")
                 val screenName: String,
                 @JsonProperty("is_closed")
                 val closed: Boolean,
                 @JsonProperty("type")
                 val type: String,
                 @JsonProperty("is_admin")
                 val admin: Boolean,
                 @JsonProperty("admin_level")
                 val adminLevel: Boolean,
                 @JsonProperty("is_member")
                 val member: Boolean,
                 val description: String?,
                 @JsonProperty("photo_50")
                 val photo50: String?,
                 @JsonProperty("photo_100")
                 val photo100: String?,
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
