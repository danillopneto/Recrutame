package ufg.go.br.recrutame.Util

import ufg.go.br.recrutame.model.JobModel

class JobUtils {
    companion object{
        fun getJobs():List<JobModel>{
            var jobs:ArrayList<JobModel> = ArrayList()
            jobs.add(JobModel("Desenvolvedor de software C#", "http://www.e-farsas.com/wp-content/uploads/google-fachada.jpg", "Goiânia - GO"));
            jobs.add(JobModel("Desenvolvedor de software Java", "http://t.i.uol.com.br/tecnologia/2012/01/20/fachada-da-boate-facebook-inaugurada-nesta-sexta-em-epitaciolandia-1327101786423_615x300.jpg", "Goiânia - GO"));
            jobs.add(JobModel("Analista de sistemas", "https://jobdescriptionshub.com/wp-content/uploads/2017/02/system-analyst.jpg", "Goiânia - GO"));
            jobs.add(JobModel("Analista de requisitos", "https://www.iag.biz/wp-content/uploads/2016/06/350-Requirements-Management-Services.jpg", "Goiânia - GO"));
            jobs.add(JobModel("Gerente de loja", "hhttp://www.bestofsampleresume.com/wp-content/uploads/2016/07/retail-store-manager-resume-sample.jpg", "Goiânia - GO"));
            jobs.add(JobModel("Motorista", "https://thumbs.dreamstime.com/b/professional-driver-putting-her-driving-gloves-woman-chauffeur-uniform-black-leather-59946931.jpg", "Goiânia - GO"));
            jobs.add(JobModel("CEO", "https://thumbs.dreamstime.com/b/ceo-looking-city-planning-rear-view-company-large-buildings-his-office-window-d-rendering-mock-up-toned-82642174.jpg", "Goiânia - GO"));
            jobs.add(JobModel("Engenheiro", "http://memi.lk/wp-content/uploads/2017/02/eng.jpg", "Goiânia - GO"));

            return jobs;
        }
    }

}