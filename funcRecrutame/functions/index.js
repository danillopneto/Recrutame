const functions = require('firebase-functions');
const admin = require('firebase-admin');
const moment = require('moment');

admin.initializeApp(functions.config().firebase);

var filterByLongitude = (geoLocation) => {
    return function(vaga) {      
        return vaga.longitude >= geoLocation.minLongitude && vaga.longitude <= geoLocation.maxLongitude;
    }
}

var filterByJobsNotApplied = (matches) => {    
    
    return function(vaga){

        matches = Object.keys(matches).map((key) => {
            return matches[key];
        }); 

        var match = matches.filter(filterMatchByJobId(vaga));

        var isAppliedToJob = match.length > 0;
        var canApplyToJob = match.some(canApplyAgain(vaga));

        var jobIsAvailable = !isAppliedToJob || canApplyToJob;

        return jobIsAvailable;
    };
}

var filterMatchByJobId = (vaga) => {
    return function(appliedJob){
        return appliedJob.jobId === vaga.id
    }
}

var canApplyAgain = (vaga) => {  
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

var sortMessages = (match) => {
    
    if(match.messages){ 
        match.messages = Object.keys(match.messages).map((key) => {
            return match.messages[key];
        });   

        match.messages = match.messages.filter(x => x);    
    }    

    return match;
}

var getLastMessageFromMatch = (match) => {
    match = sortMessages(match);

    var lastMessage = match.messages[match.messages.length - 1];

    return {
        message: lastMessage.message,        
        sendByUser: lastMessage.sendByUser,
        companyName: match.companyName,
        companyImg: match.companyImg,
        jobId: match.jobId
    }
}

var getGeoLocation = (data) => {
    const { minLatitude = 0, maxLatitude = 0, minLongitude = 0, maxLongitude = 0 } = data;

    var geoLocation = { minLatitude, maxLatitude, minLongitude, maxLongitude };

    return geoLocation;
}

var getMatchesByUserId = (userId) => {
    return admin
        .database()
        .ref(`/matches/${userId}`);
}

var getJobsByLatitude = (geoLocation) => {
    return admin
    .database()
    .ref('/vagas')
    .orderByChild("latitude")    
    .startAt(parseFloat(geoLocation.minLatitude))
    .endAt(parseFloat(geoLocation.maxLatitude))  
    .once('value');
}

var filterJobsByMatch = (snap, vagas) => {
    var matches = snap.val();

    if(matches){
        return JSON.stringify(vagas.filter(filterByJobsNotApplied(matches)));
    }            

    return JSON.stringify(vagas);
}

var getAvailableJobs = (snapshot, userId, geoLocation) =>{
    let vagas = snapshot.val();    

    if(!vagas){
        return null;
    }
    
    let vagasFiltradas = vagas.filter(filterByLongitude(geoLocation));

    return getMatchesByUserId(userId).once('value').then((snap) => filterJobsByMatch(snap, vagasFiltradas)); 
}

var getMatchesByUserIdAndJobId = (userId, jobId) => {
    return getMatchesByUserId(userId).orderByChild("jobId").equalTo(parseInt(jobId)).once('value');
}

var filterMessages = (snap)=>{
    var match = snap.val();
          
    if(match){
        console.log({match : JSON.stringify(match)})

        match = match[Object.keys(match)[0]];

        match.messages = Object.keys(match.messages).map((key) => {
            return match.messages[key];
        });

        return JSON.stringify(match);
    }            

    return null;
}

var filterDataFromLastMessage = (snap)=>{
    var matches = snap.val();    
          
    if(matches){
        matches = Object.keys(matches).map((key) => {
            return matches[key];
        }); 

        return JSON.stringify(matches.filter(x => x.messages).map(getLastMessageFromMatch));
    }            

    return null;
}

var getMessagesByMatch = functions.https.onCall((data, context)=>{
      const { userId } = data;
      const { jobId } = data;

      return getMatchesByUserIdAndJobId(userId, jobId).then(filterMessages); 
});

var getMatches = functions.https.onCall((data, context)=>{
      const { userId } = data;

      return getMatchesByUserId(userId).once('value').then(filterDataFromLastMessage); 
});

var getJobOffers = functions.https.onCall((data, context) => {
    var geoLocation = getGeoLocation(data);
    const userId = data.userId;
 
    return getJobsByLatitude(geoLocation).then((snapshot) => getAvailableJobs(snapshot, userId, geoLocation));
  });

  var getJobOffersLocal = functions.https.onRequest((req, res) => {
    var geoLocation = getGeoLocation(req.query);
    const { userId } = req.query; 
 
    return getJobsByLatitude(geoLocation).then((snapshot) => getAvailableJobs(snapshot, userId, geoLocation))
    .then(snapshot => {
        return res.status(200).send(JSON.parse(snapshot));
      });
  });

  var sendMessage = functions.https.onRequest((req, res) => {
        const { userId, jobId, message } = req.query;        

        var finalMessage = {
            date: moment().format("DD/MM/YY HH:mm:ss"),
            message: message,
            sendByUser: false          
        }

        var rootRef = admin.database().ref();
        var messagesRef = rootRef.child(`/matches/${userId}/${jobId}/messages`);
        var newMessage = messagesRef.push();
        newMessage.set(finalMessage);

        return res.status(200).send(finalMessage);
        
  });

  var getMatchesByMatchHttps = functions.https.onRequest((req, res)=>{
    const { userId, jobId } = req.query;
    
    return getMatchesByUserIdAndJobId(userId, jobId).then(filterMessages).then(data=>{
        return res.status(200).send(data);
    }); 
});
  

  exports.getJobOffers = getJobOffers;
  exports.getMatches = getMatches;
  exports.getMessagesByMatch = getMessagesByMatch;
  exports.getJobOffersLocal = getJobOffersLocal
  exports.sendMessage = sendMessage
  exports.getMatchesByMatchHttps = getMatchesByMatchHttps