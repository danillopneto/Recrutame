package ufg.go.br.recrutame.model

class JobModel {
    lateinit var key: String
    lateinit var title:String
    lateinit var image: String
    lateinit var city: String
    lateinit var state: String
    lateinit var country: String
    lateinit var type: String
    var salary: Double = 0.0
    lateinit var description: String
    lateinit var company:String
    var latitude: Double = 0.0
    var longitude: Double = 0.0
}