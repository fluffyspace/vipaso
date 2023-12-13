package eu.kodba.vipaso.models

data class Repository (
    var name: String,
    var id: Int,
    var owner: User?,
    var description: String?,
)