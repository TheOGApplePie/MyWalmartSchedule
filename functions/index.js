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
    
    return new Promise((resolve, reject) => {
        var schedule = {}
        var csvStream = csv.parseFile(tempFileDest, {'headers': true});
    csvStream.on('data', (data) => {
        csvStream.pause();
        var store = data.Store;
        var job = data.Job;
        var employee = data.Employee;
        schedule[employee] = [];
        schedule[employee].push(store);
        schedule[employee].push(job);
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
    }).on('end', ()=> {
        resolve (schedule);
        console.log("Successfully parsed CSV file.");
    }).on('error', (error)=> {
        reject(error);
        console.log(error);
    })
    
    }).then((schedule) => {
        // Separating Employees from their shifts
        const employees = Object.keys(schedule);
        const shifts = Object.keys(schedule).map((key)=> {
            return schedule[key];
        });
        const promises = [];
        var storeNumber;
        var jobTitle;
        var employeeArray = [];

        // Writing the shifts to the database
        for (var i = 0; i < employees.length; i++){
            for (var j = 2; j < shifts[i].length; j += 2){
                storeNumber = shifts[i][0];
                jobTitle = shifts[i][1];
                const p = db.collection(storeNumber).doc(jobTitle).collection(employees[i]).add({
                    begin: shifts[i][j],
                    end: shifts[i][j+1]
                });                    
                promises.push(p);
            }
            employeeArray.push(employees[i]);
        }
        const arrayData = {Associates: employeeArray}

        // Updating the list of associates in the department
        const associatesListPromise = db.collection(storeNumber).doc(jobTitle).set(arrayData);
        promises.push(associatesListPromise);
        db.collection(storeNumber).doc(jobTitle).collection("Tokens").get().then(snapshot => {
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
            for(var i = 0; i < regTokens.length; i++){
            const p = admin.messaging().sendToDevice(regTokens[i].token, payload);
            promises.push(p);
            }
            return Promise.all(promises);
          }).catch(err=>{console.log(err)});
          return Promise.all(promises);
    })
    .then(() => {
        console.log("File deleted from Cloud Storage.")
        return file.delete();
    }).catch(err =>{console.log(err)});
})
});