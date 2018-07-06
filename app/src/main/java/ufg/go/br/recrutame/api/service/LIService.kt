package ufg.go.br.recrutame.api.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ufg.go.br.recrutame.API_KEY
import ufg.go.br.recrutame.GRANT_TYPE
import ufg.go.br.recrutame.REDIRECT_URI
import ufg.go.br.recrutame.SECRET_KEY
import ufg.go.br.recrutame.api.model.AccessToken

interface LIService {
    @FormUrlEncoded
    @POST("/oauth/v2/accessToken")
    fun getNewAccessToken(
            @Field("code") code: String,
            @Field("grant_type") grant_type: String = GRANT_TYPE,
            @Field("redirect_uri") redirect_uri: String = REDIRECT_URI,
            @Field("client_id") client_id: String = API_KEY,
            @Field("client_secret") client_secret: String = SECRET_KEY): Call<AccessToken>

    @FormUrlEncoded
    @POST("/oauth/v2/accessToken")
    fun getRefreshAccessToken(
            @Field("code") code: String,
            @Field("grant_type") grant_type: String = GRANT_TYPE,
            @Field("redirect_uri") redirect_uri: String = REDIRECT_URI,
            @Field("client_id") client_id: String = API_KEY,
            @Field("client_secret") client_secret: String = SECRET_KEY): Call<AccessToken>
}