package ufg.go.br.recrutame.api.service

import retrofit2.Call
import retrofit2.http.*
import ufg.go.br.recrutame.CLIENT_ID
import ufg.go.br.recrutame.GRANT_TYPE
import ufg.go.br.recrutame.REDIRECT_URI
import ufg.go.br.recrutame.CLIENT_SECRET
import ufg.go.br.recrutame.api.model.LIAccessToken
import ufg.go.br.recrutame.api.model.LIProfileInfo

interface LIService {
    @FormUrlEncoded
    @POST("/oauth/v2/accessToken")
    fun getNewAccessToken(
            @Field("code") code: String,
            @Field("grant_type") grant_type: String = GRANT_TYPE,
            @Field("redirect_uri") redirect_uri: String = REDIRECT_URI,
            @Field("client_id") client_id: String = CLIENT_ID,
            @Field("client_secret") client_secret: String = CLIENT_SECRET): Call<LIAccessToken>

    @GET("/v1/people/~:(id,first-name,last-name,picture-url,public-profile-url,email-address)?format=json")
    fun getUserInfo(
            @HeaderMap headers: Map<String, String> ): Call<LIProfileInfo>
}