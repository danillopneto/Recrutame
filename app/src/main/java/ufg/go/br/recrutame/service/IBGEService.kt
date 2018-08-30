package ufg.go.br.recrutame.service

import retrofit2.Call
import retrofit2.http.*
import ufg.go.br.recrutame.model.api.CityInfo
import ufg.go.br.recrutame.model.api.UFInfo

interface IBGEService {
    @Headers("Content-Type: application/json")
    @GET("/api/v1/localidades/estados")
    fun getUFs(): Call<List<UFInfo>>

    @Headers("Content-Type: application/json")
    @GET("/api/v1/localidades/estados/{UF}/municipios")
    fun getCities(@Path("UF") code: String): Call<List<CityInfo>>
}