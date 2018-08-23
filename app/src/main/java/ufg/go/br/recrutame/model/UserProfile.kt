package ufg.go.br.recrutame.model

data class UserProfile(val generalInfo: UserGeneralInfo, val contactInfo: UserContactInfo) {
    constructor() : this(UserGeneralInfo(), UserContactInfo())
}