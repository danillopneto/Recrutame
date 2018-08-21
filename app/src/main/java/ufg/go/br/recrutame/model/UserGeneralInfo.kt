package ufg.go.br.recrutame.model

data class UserGeneralInfo(val name: String, val lastName: String, val birthdate: Int?, val city: String, val state: String) {
    constructor() : this("", "", null, "", "")

    fun getBirthDateFormated(): String {
        if (birthdate != null) {
            val dateAsString = birthdate.toString()
            val day = dateAsString.substring(6, 8)
            val month = dateAsString.substring(4, 6)
            val year = dateAsString.substring(0, 4)
            return "$day/$month/$year"
        }

        return ""
    }
}