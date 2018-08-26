package ufg.go.br.recrutame.model

data class UserProfile(val generalInfo: UserGeneralInfo, val contactInfo: UserContactInfo, val experiences: HashMap<String, UserExperienceInfo>, val languages: HashMap<String, UserLanguageInfo>) {
    constructor() : this(UserGeneralInfo(), UserContactInfo(), HashMap<String, UserExperienceInfo>(), HashMap<String, UserLanguageInfo>())
}