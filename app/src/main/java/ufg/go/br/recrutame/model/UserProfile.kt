package ufg.go.br.recrutame.model

data class UserProfile(val generalInfo: UserGeneralInfo) {
    constructor() : this(UserGeneralInfo())
}