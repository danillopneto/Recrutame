package ufg.go.br.recrutame.api.model


class AccessToken {

    var accessToken: String? = null
    private var expires_in: Int? = null
    var refreshToken: String? = null

    var expiry: Int
        get() = expires_in!!
        set(expires_in) {
            this.expires_in = expires_in
        }
}
