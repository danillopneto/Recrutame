package ufg.go.br.recrutame.api.service

import android.content.Context
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ufg.go.br.recrutame.API_BASE_URL
import ufg.go.br.recrutame.BuildConfig
import ufg.go.br.recrutame.REDIRECT_URI
import ufg.go.br.recrutame.api.model.AccessToken
import java.io.IOException

class ServiceGenerator {

    private var httpClient: OkHttpClient.Builder? = null

    private var builder: Retrofit.Builder? = null

    private var mContext: Context? = null
    private var mToken: AccessToken? = null

    fun <S> createService(serviceClass: Class<S>): S {
        httpClient = OkHttpClient.Builder()
        builder = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        val client = httpClient!!.build()
        val retrofit = builder!!.client(client).build()
        return retrofit.create(serviceClass)
    }

    fun <S> createService(serviceClass: Class<S>, accessToken: AccessToken?, c: Context): S {
        httpClient = OkHttpClient.Builder()
        builder = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        if (accessToken != null) {
            mContext = c
            mToken = accessToken
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
                val call = tokenClient.getRefreshAccessToken(mToken!!.refreshToken!!)
                try {
                    val tokenResponse = call.execute()
                    if (tokenResponse.code() == 200) {
                        val newToken = tokenResponse.body()
                        mToken = newToken
                        val prefs = mContext!!.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                        prefs.edit().putBoolean("oauth.loggedin", true).apply()
                        prefs.edit().putString("oauth.accesstoken", newToken!!.accessToken).apply()
                        prefs.edit().putString("oauth.refreshtoken", newToken!!.refreshToken).apply()

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
        var response = response
        var result = 1
        while ((response!!.priorResponse()) != null) {
            result++
        }
        return result
    }
}
