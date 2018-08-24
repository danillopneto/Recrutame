package ufg.go.br.recrutame.model

data class UserProfile(val generalInfo: UserGeneralInfo, val contactInfo: UserContactInfo, val languages: List<UserLanguageInfo>) {
    constructor() : this(UserGeneralInfo(), UserContactInfo(), mutableListOf<UserLanguageInfo>())
}