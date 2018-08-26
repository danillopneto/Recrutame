package ufg.go.br.recrutame.model

class UserExperienceInfo(val title: String, val company: String, val startDate: Int?, val endDate: Int?) {
    constructor() : this("", "", null, null)
}