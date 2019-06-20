const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
var db = admin.firestore();
const os = require( 'os');
const path = require('path');
const csv = require('fast-csv');

exports.uploadFile = functions.storage.object().onFinalize(object => {

const filePath = object.name; // File path in the bucket.
const fileName = path.basename(filePath);
const tempFileDest = path.join(os.tmpdir(), fileName);


const bucket = admin.storage().bucket(object.bucket);
const file = bucket.file(filePath);

// Download file from bucket
return file.download({destination: tempFileDest})
.then(() => {            
    
    return new Promise(function (resolve, reject) {
        var schedule = {};
        var csvStream = csv.fromPath(tempFileDest, {'headers': true});
    csvStream.on('data', function(data){
        csvStream.pause();
        var employee = data.Employee;
        schedule[employee] = [];
        var satS   = new Date (data.SatS);
        schedule[employee].push(satS);
        var satF   = new Date (data.SatF);
        schedule[employee].push(satF);
        var sunS   = new Date (data.SunS);
        schedule[employee].push(sunS);
        var sunF   = new Date (data.SunF);
        schedule[employee].push(sunF);
        var monS   = new Date (data.MonS);
        schedule[employee].push(monS);
        var monF   = new Date (data.MonF);
        schedule[employee].push(monF);
        var tuesS  = new Date (data.TuesS);
        schedule[employee].push(tuesS);
        var tuesF  = new Date (data.TuesF);
        schedule[employee].push(tuesF);
        var wedS   = new Date (data.WedS);
        schedule[employee].push(wedS);
        var wedF   = new Date (data.WedF);
        schedule[employee].push(wedF);
        var thursS = new Date (data.ThursS);
        schedule[employee].push(thursS);
        var thursF = new Date (data.ThursF);
        schedule[employee].push(thursF);
        var friS   = new Date (data.FriS);
        schedule[employee].push(friS);
        var friF   = new Date (data.FriF);
        schedule[employee].push(friF);
        csvStream.resume();
    }).on('end', function(){
        resolve (schedule);
        console.log("Successfully parsed CSV file.");
    }).on('error', function(error){
        reject(error);
        console.log(error);
    })
    
    }).then((schedule) =>{
        const employees = Object.keys(schedule);
        const shifts = Object.keys(schedule).map(function(key) {
             return schedule[key];
         });
        const promises = [];
        for (var i = 0; i < employees.length; i++){
            for (var j = 0; j < shifts[i].length; j += 2){
                const p = db.collection(employees[i]).add({
                    begin: shifts[i][j],
                    end: shifts[i][j+1]
                });                    
                promises.push(p);
            }
        }
        return Promise.all(promises);
    }).then(() => {
        console.log("File deleted from Cloud Storage.")
        return file.delete();
    }).catch(err =>{console.log(err)});
})
});
exports.sendNotification = functions.storage.object().onDelete(object =>{
    db.collection('tokens').get()
    .then(snapshot => {
        var regTokens = [];       
            snapshot.forEach(doc => {
                regTokens.push(doc.data());
              });              
              var payload = {
                notification:{
                    title: "New Schedule Available",
                    body: "The next week's schedule has been released! Check it out so you can know when you're working next."
                }
            };
        const promises = [];
        for(var i = 0; i < regTokens.length; i++){
        const p = admin.messaging().sendToDevice(regTokens[i].token, payload);
        promises.push(p);
        }
        console.log("Notifications sent!")
        return Promise.all(promises);
      }).catch(err => {
        console.log(err);
      })
    });