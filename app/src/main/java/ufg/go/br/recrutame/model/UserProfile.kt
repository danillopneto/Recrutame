package ufg.go.br.recrutame.model

data class UserProfile(val generalInfo: UserGeneralInfo, val contactInfo: UserContactInfo, val languages: HashMap<String, UserLanguageInfo>) {
    constructor() : this(UserGeneralInfo(), UserContactInfo(), HashMap<String, UserLanguageInfo>())
}