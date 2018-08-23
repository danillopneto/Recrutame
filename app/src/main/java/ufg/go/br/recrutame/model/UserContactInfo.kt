package ufg.go.br.recrutame.model

data class UserContactInfo(val email: String, val webSite: String, val phone: String, val phoneType: String) {
    constructor() : this("", "", "", "")
}