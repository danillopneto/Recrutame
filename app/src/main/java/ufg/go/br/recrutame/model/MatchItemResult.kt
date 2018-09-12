package ufg.go.br.recrutame.model

import ufg.go.br.recrutame.enum.EnumStatusProcessoSeletivo

class MatchItemResult(
        var appliedDate:String = "",
        var companyImg: String = "",
        var companyName:String = "",
        var jobId: Long = 0,
        var status: EnumStatusProcessoSeletivo,
        var messages:HashMap<String, Message>) {
    constructor():this("","","",0, EnumStatusProcessoSeletivo.PENDING, HashMap<String, Message>())
}