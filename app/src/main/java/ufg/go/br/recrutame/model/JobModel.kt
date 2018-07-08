package ufg.go.br.recrutame.model

class JobModel(title:String, image: String, location: String) {
    var title: String? = null;
    var image:String? = null
    var location:String? = null

    init{
        this.title = title;
        this.image = image;
        this.location = location
    }

}