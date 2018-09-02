package ufg.go.br.recrutame.model

data class UserProfile(val generalInfo: UserGeneralInfo, val contactInfo: UserContactInfo, val experiences: HashMap<String, UserExperienceInfo>, val educations: HashMap<String, UserEducationInfo>, val languages: HashMap<String, UserLanguageInfo>) {
    constructor() : this(UserGeneralInfo(), UserContactInfo(), HashMap<String, UserExperienceInfo>(), HashMap<String, UserEducationInfo>(), HashMap<String, UserLanguageInfo>())
}