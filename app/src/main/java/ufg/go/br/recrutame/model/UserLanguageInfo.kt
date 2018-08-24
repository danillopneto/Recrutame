package ufg.go.br.recrutame.model

import ufg.go.br.recrutame.enum.EnumLanguageLevel

data class UserLanguageInfo(val language: String, val level: EnumLanguageLevel?) {
    constructor() : this("", null)
}