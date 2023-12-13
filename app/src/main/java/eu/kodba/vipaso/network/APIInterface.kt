package eu.kodba.vipaso.network

import eu.kodba.vipaso.models.Repository
import eu.kodba.vipaso.models.UserList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface APIInterface {
    @GET("search/users")
    fun doGetUserList(@Query("q") q: String, @Query("page") page: Int): Call<UserList>

    @GET("https://api.github.com/users/{username}/starred")
    fun doGetUserStarredRepositories(@Path("username") username: String): Call<List<Repository>>

}