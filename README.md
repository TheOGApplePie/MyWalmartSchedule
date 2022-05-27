# MyWalmartSchedule
This is a fun little project that made use of the Android platform (in java) with Firebase Firestore as the database.
My Walmart Schedule is a project I embarked on to solve the problem of not being able to access your schedule from home. The idea was simple: 

The employee would be able to sign into the app and be able to view their schedule for the day (along with other colleagues in the same department) and also see their schedules posted in the past and future.

The employer would access the server and upload their department's schedule to the cloud and the cloud (via cloud functions) would parse the uploaded schedule and notify employees when a new schedule has been posted.

This project (as described above) required two components: A server side, as well as a client side. The server side has been written in node.js and the client side (at least for this part) has been written in Java for Android.

This project has been discontinued since Walmart Canada did come out with an official app linked with their schedule generator.
