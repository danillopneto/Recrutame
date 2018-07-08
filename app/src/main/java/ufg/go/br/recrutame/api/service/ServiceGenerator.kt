package ufg.go.br.recrutame.api.service

import android.content.Context
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ufg.go.br.recrutame.BuildConfig
import ufg.go.br.recrutame.OAUTH_ACCESSTOKEN
import ufg.go.br.recrutame.OAUTH_LOGGEDIN
import ufg.go.br.recrutame.api.model.LIAccessToken
import java.io.IOException

class ServiceGenerator {
    var apiBaseUrl: String

    private var httpClient: OkHttpClient.Builder? = null

    private var builder: Retrofit.Builder? = null

    private var mContext: Context? = null
    private var mToken: LIAccessToken? = null

    constructor(apiBaseUrl: String) {
        this.apiBaseUrl = apiBaseUrl
    }

    fun <S> createService(serviceClass: Class<S>): S {
        httpClient = OkHttpClient.Builder()
        builder = Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())

        val client = httpClient!!.build()
        val retrofit = builder!!.client(client).build()
        return retrofit.create(serviceClass)
    }

    fun <S> createService(serviceClass: Class<S>, LIAccessToken: LIAccessToken?, c: Context): S {
        httpClient = OkHttpClient.Builder()
        builder = Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())

        if (LIAccessToken != null) {
            mContext = c
            mToken = LIAccessToken
            httpClient!!.addInterceptor { chain ->
                val original = chain.request()

                val requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-type", "application/json")
                        .method(original.method(), original.body())

                val request = requestBuilder.build()
                chain.proceed(request)
            }

            httpClient!!.authenticator(Authenticator { route, response ->
                if (responseCount(response) >= 2) {
                    // If both the original call and the call with refreshed token failed,
                    // it will probably keep failing, so don't try again.
                    return@Authenticator null
                }

                // We need a new client, since we don't want to make another call using our client with access token
                val tokenClient = createService(LIService::class.java)
                val call = tokenClient.getNewAccessToken(mToken!!.accessToken!!)
                try {
                    val tokenResponse = call.execute()
                    if (tokenResponse.code() == 200) {
                        val newToken = tokenResponse.body()
                        mToken = newToken
                        val prefs = mContext!!.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                        prefs.edit().putBoolean(OAUTH_LOGGEDIN, true).apply()
                        prefs.edit().putString(OAUTH_ACCESSTOKEN, newToken!!.accessToken).apply()

                        return@Authenticator response.request().newBuilder()
                                .build()
                    } else {
                        return@Authenticator null
                    }
                } catch (e: IOException) {
                    return@Authenticator null
                }
            })
        }

        val client = httpClient!!.build()
        val retrofit = builder!!.client(client).build()
        return retrofit.create(serviceClass)
    }

    private fun responseCount(response: Response?): Int {
        val response = response
        var result = 1
        while ((response!!.priorResponse()) != null) {
            result++
        }
        return result
    }
}
