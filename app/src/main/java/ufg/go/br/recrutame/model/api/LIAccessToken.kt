package ufg.go.br.recrutame.model.api

import com.google.gson.annotations.SerializedName

class LIAccessToken {

    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("expires_in")
    private var expiresIn: Int? = null
}
