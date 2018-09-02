package ufg.go.br.recrutame.model

import ufg.go.br.recrutame.enum.EnumStatusProcessoSeletivo

class Match(
        var companyName:String = "",
        var appliedDate:String = "",
        var jobId: Long = 0,
        var companyImg: String = "",
        var status: EnumStatusProcessoSeletivo,
        var messages:List<Message>) {

}