package ufg.go.br.recrutame.api.model

data class UFInfo(val id: String, val nome: String, val sigla: String) {
    constructor() : this("", "", "")
}