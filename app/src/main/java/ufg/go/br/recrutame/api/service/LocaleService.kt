package ufg.go.br.recrutame.api.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import ufg.go.br.recrutame.api.model.UFInfo

interface LocaleService {
    @Headers("Content-Type: application/json")
    @GET("/api/v1/localidades/estados")
    fun getUFs(): Call<List<UFInfo>>
}