package ufg.go.br.recrutame.model

import ufg.go.br.recrutame.enum.EnumStatusProcessoSeletivo

class Match(
        var appliedDate:String = "",
        var companyImg: String = "",
        var companyName:String = "",
        var jobId: Long = 0,
        var status: EnumStatusProcessoSeletivo,
        var messages:List<Message>) {
    constructor():this("","","",0, EnumStatusProcessoSeletivo.PENDING, ArrayList<Message>())
}