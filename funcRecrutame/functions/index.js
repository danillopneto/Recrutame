const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

var filtroPorDistancia = function(geoLocation){
    return function(vaga) {
        var filtroPorLatitude = vaga.latitude >= geoLocation.minLatitude && vaga.latitude <= geoLocation.maxLatitude;         
        var filtroPorLongitude = vaga.longitude >= geoLocation.minLongitude && vaga.longitude <= geoLocation.maxLongitude;

        return filtroPorLatitude && filtroPorLongitude;
    }
}

var filtroPorVagasNaoAplicadas = function(user){
    return function(vaga){
        return !user.appliedJobs.some(filtroNaoEstaCandidatado(vaga)) || user.appliedJobs.some(filtroPodeCandidatarNovamente(vaga));
    };
}

var filtroNaoEstaCandidatado = function(vaga){
    return function(appliedJob){
        return appliedJob.jobId === vaga.id;
    }
}

var filtroPodeCandidatarNovamente = function(vaga){  
    const daysToApplyAgain = 30;

    return function(appliedJob){
        var today = new Date();
        today.setHours(0, 0, 0, 0);   

        var appliedDate = appliedJob.appliedDate.split('/');
        var dateToApply = new Date(appliedDate[2], parseInt(appliedDate[1])-1, appliedDate[0]);
        dateToApply.setDate(dateToApply.getDate() + (vaga.daysToApplyAgain || daysToApplyAgain));

        return appliedJob.jobId === vaga.id && today.getTime() >= dateToApply.getTime();
    }
}

exports.vagasNaoCandidatadas = functions.https.onCall((data, context) => {
    if (!context.auth) {
        throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
            'while authenticated.');
      }

    const minLatitude = data.minLatitude || 0;
    const maxLatitude = data.maxLatitude || 0;
    const minLongitude = data.minLongitude || 0;
    const maxLongitude = data.maxLongitude || 0;

    var geoLocation = { minLatitude, maxLatitude, minLongitude, maxLongitude };

    const userId = data.userId;
 
    return admin.database().ref('/vagas').once('value').then((snapshot) => {
        let vagas = snapshot.val().filter(filtroPorDistancia(geoLocation));

        return admin.database().ref('/users/' + userId).once('value').then((snap) => {
            var user = snap.val();
            
            if(user && user.appliedJobs){
                return JSON.stringify(vagas.filter(filtroPorVagasNaoAplicadas(user)));
            }            

            return JSON.stringify(vagas);
        });      
    });
  });