package ufg.go.br.recrutame.model

data class UserEducationInfo(val key: String, val degree: String, val school: String, val startDate: Int?, val endDate: Int?) {
    constructor() : this("", "","",null, null)
}