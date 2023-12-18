package otus.gpb.coroutines.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import otus.gpb.coroutines.network.data.LoginResponse
import otus.gpb.coroutines.network.data.Post
import otus.gpb.coroutines.network.data.Profile
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Базовый URL для нашего сервиса
 */
private const val BASE_URL = "https://my-json-server.typicode.com/Android-Developer-Basic/Coroutines/"

/**
 * API сервера
 */
interface Api {
    @GET("login")
    fun login(@Query("login") login: String, @Query("password") password: String): Call<LoginResponse>

    @GET("profile")
    fun getProfile(@Header("X-Auth-Token") token: String, @Query("id") id: Long): Call<Profile>

    @GET("posts")
    fun getPosts(@Header("X-Auth-Token") token: String): Call<Post>

    companion object {
        /**
         * Создает сервис
         */
        fun create(): Api {
            val okHttp = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            val json = Json {
                coerceInputValues = true
                ignoreUnknownKeys = true
            }

            val retrofit = Retrofit.Builder()
                .client(okHttp)
                .baseUrl(BASE_URL)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()

            return retrofit.create(Api::class.java)
        }
    }
}