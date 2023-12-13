package eu.kodba.vipaso.models

import com.google.gson.annotations.SerializedName

data class UserList (
    @SerializedName("total_count")
    var total_count: Int? = null,
    @SerializedName("incomplete_results")
    var incomplete_results: Boolean? = null,
    @SerializedName("items")
    var items: List<User>? = null,
    @SerializedName("errors")
    var errors: List<Error>? = null,
    @SerializedName("message")
    var message: String? = null,
)

data class Error(
    var message: String,
    var resource: String,
    var field: String,
    var code: String
)
