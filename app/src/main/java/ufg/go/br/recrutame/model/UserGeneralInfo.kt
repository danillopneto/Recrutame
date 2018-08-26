package ufg.go.br.recrutame.model

data class UserGeneralInfo(val name: String, val lastName: String, val birthdate: Int?, val gender: String, val state: String, val city: String) {
    constructor() : this("", "", null, "", "", "")
}