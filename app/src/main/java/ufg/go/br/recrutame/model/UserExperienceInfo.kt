package ufg.go.br.recrutame.model

class UserExperienceInfo(val key: String, val title: String, val company: String, val startDate: Int?, val endDate: Int?) {
    constructor() : this("","", "", null, null)
}