package ufg.go.br.recrutame.service

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceGenerator(private var apiBaseUrl: String) {

    private var httpClient: OkHttpClient.Builder? = null

    private var builder: Retrofit.Builder? = null

    fun <S> createService(serviceClass: Class<S>): S {
        httpClient = OkHttpClient.Builder()
        builder = Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())

        val client = httpClient!!.build()
        val retrofit = builder!!.client(client).build()
        return retrofit.create(serviceClass)
    }
}
