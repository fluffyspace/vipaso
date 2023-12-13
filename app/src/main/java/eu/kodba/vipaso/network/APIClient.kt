package eu.kodba.vipaso.network

import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator : Authenticator {
    var token = "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"
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") != null) {
            return null
        }
        return response.request.newBuilder().header("Authorization", "Bearer " + token).build()
    }
}

class APIClient {
    val interceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder().addHeader("X-GitHub-Api-Version", "2022-11-28").addHeader("Accept", "application/vnd.github+json").build()
            chain.proceed(request)
        }
        .authenticator(TokenAuthenticator())
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val apiInterface: APIInterface by lazy {
        retrofit.create(APIInterface::class.java)
    }

}