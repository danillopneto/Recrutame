package ufg.go.br.recrutame.model

data class UserGeneralInfo(val name: String, val lastName: String, val birthdate: Int?, val gender: String, val city: String, val state: String) {
    constructor() : this("", "", null, "", "", "")
}