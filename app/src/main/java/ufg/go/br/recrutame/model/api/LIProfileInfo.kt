package ufg.go.br.recrutame.model.api

class LIProfileInfo {
    var id: String? = null

    var firstName: String? = null

    var lastName: String? = null

    var emailAddress: String? = null

    var pictureUrl: String? = null

    var publicProfileUrl: String? = null

    fun getFullName(): String {
        return "$firstName $lastName"
    }
}