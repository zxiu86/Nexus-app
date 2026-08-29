package com.example.data.network

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object GitHubNetworkModule {

    private const val GITHUB_API_BASE_URL = "https://api.github.com/"

    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()
            .header("User-Agent", "Nexus-Manga-App-Android/9.1.1")

        // Retrieve token safely from BuildConfig if provided in .env
        val token = runCatching {
            BuildConfig::class.java.getField("GITHUB_TOKEN").get(null) as? String
        }.getOrNull()?.trim()

        if (!token.isNullOrEmpty() && token != "null") {
            val authHeader = if (token.startsWith("Bearer ") || token.startsWith("token ")) {
                token
            } else {
                "Bearer $token"
            }
            builder.header("Authorization", authHeader)
        }

        chain.proceed(builder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(GITHUB_API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: GitHubApiService = retrofit.create(GitHubApiService::class.java)

    /**
     * Helper to get configured GitHub owner (fallback to default if not set)
     */
    fun getConfiguredOwner(): String {
        val owner = runCatching {
            BuildConfig::class.java.getField("GITHUB_OWNER").get(null) as? String
        }.getOrNull()?.trim()
        return if (!owner.isNullOrEmpty() && owner != "null") owner else "nexus-manga"
    }

    /**
     * Helper to get configured GitHub repository (fallback to default if not set)
     */
    fun getConfiguredRepo(): String {
        val repo = runCatching {
            BuildConfig::class.java.getField("GITHUB_REPO").get(null) as? String
        }.getOrNull()?.trim()
        return if (!repo.isNullOrEmpty() && repo != "null") repo else "nexus-data"
    }

    /**
     * Helper to get target branch (default: main)
     */
    fun getConfiguredBranch(): String {
        val branch = runCatching {
            BuildConfig::class.java.getField("GITHUB_BRANCH").get(null) as? String
        }.getOrNull()?.trim()
        return if (!branch.isNullOrEmpty() && branch != "null") branch else "main"
    }

    /**
     * Check if user has configured custom repository settings
     */
    fun isGitHubConfigured(): Boolean {
        val token = runCatching {
            BuildConfig::class.java.getField("GITHUB_TOKEN").get(null) as? String
        }.getOrNull()?.trim()
        val owner = runCatching {
            BuildConfig::class.java.getField("GITHUB_OWNER").get(null) as? String
        }.getOrNull()?.trim()
        val repo = runCatching {
            BuildConfig::class.java.getField("GITHUB_REPO").get(null) as? String
        }.getOrNull()?.trim()

        return (!token.isNullOrEmpty() && token != "null") ||
                (!owner.isNullOrEmpty() && owner != "null" && owner != "nexus-manga")
    }
}
