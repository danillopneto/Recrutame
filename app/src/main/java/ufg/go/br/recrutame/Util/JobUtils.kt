package ufg.go.br.recrutame.Util

import ufg.go.br.recrutame.model.JobModel

class JobUtils {
    companion object{
        fun getJobs():List<JobModel>{
            var jobs:ArrayList<JobModel> = ArrayList()
            jobs.add(JobModel("Desenvolvedor de software C#", "http://cache.johnchow.com/wp-content/uploads/2016/06/13350258_10157094827670571_330990761978567897_o.jpg", "São Paulo - SP", "CLT",3000.0, "Desenvolver sistemas web com ASP.NET Core.", "Google" ));
            jobs.add(JobModel("Desenvolvedor de software Java", "https://icdn2.digitaltrends.com/image/facebook_sign_feat-1500x844.jpg?ver=1", "São Francisco - CA, USA", "CLT",3000.0, "Desenvolver sistemas web com tecnologia Java.", "Facebook"));
            jobs.add(JobModel("Analista de sistemas", "https://jobdescriptionshub.com/wp-content/uploads/2017/02/system-analyst.jpg", "Goiânia - GO", "CLT",3000.0, "Projetar e desenvolver sistemas para Linux.", "PC Sistemas"));
            jobs.add(JobModel("Analista de requisitos", "https://www.iag.biz/wp-content/uploads/2016/06/350-Requirements-Management-Services.jpg", "Goiânia - GO", "CLT",3000.0, "Especificar e definir os requisitos do sistema.", "DataRey"));
            jobs.add(JobModel("Gerente de loja", "http://2.bp.blogspot.com/-FJWd0my3kvY/Ueflw9aFA3I/AAAAAAAAAJ0/QmxWlxeIRpk/s1600/041209_011.jpg", "Goiânia - GO", "CLT",3000.0, "Gerenciar a maior loja de produtos naturais de Goiânia.", "Natureba"));
            jobs.add(JobModel("Motorista", "https://thumbs.dreamstime.com/b/professional-driver-putting-her-driving-gloves-woman-chauffeur-uniform-black-leather-59946931.jpg", "Goiânia - GO", "CLT",4000.0, "Atuar como motorista particular 12h por dia.", "99 POP"));
            jobs.add(JobModel("CEO", "https://thumbs.dreamstime.com/b/ceo-looking-city-planning-rear-view-company-large-buildings-his-office-window-d-rendering-mock-up-toned-82642174.jpg", "Goiânia - GO", "CLT",13000.0, "Presidir e administrar a empresa", "MVR"));
            jobs.add(JobModel("Engenheiro civil", "http://memi.lk/wp-content/uploads/2017/02/eng.jpg", "Goiânia - GO", "CLT",8000.0, "Projetar prédios", "MVR"));

            return jobs;
        }
    }

}