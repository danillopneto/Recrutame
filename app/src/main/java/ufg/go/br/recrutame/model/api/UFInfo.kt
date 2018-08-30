package ufg.go.br.recrutame.model.api

data class UFInfo(val id: String, val nome: String, val sigla: String) {
    constructor() : this("", "", "")
}