package com.jpapps

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.Timestamp
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.*
import java.io.FileInputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.swing.text.DateFormatter

fun main(args: Array<String>) {

    val formatter = SimpleDateFormat("dd/MM")
    val date = Timestamp.now()
    print(formatter.format(date.toDate()))



//    val serviceAccount = FileInputStream("src/r-sports-backend-firebase-adminsdk-t7d2b-e1908f0145.json")
//    val options = FirebaseOptions.Builder()
//        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//        .setDatabaseUrl("https://r-sports-backend.firebaseio.com")
//        .build()
//    FirebaseApp.initializeApp(options)
//
//    var alert = ApsAlert.builder().setBody("This push is coming from Heroku and sending to firebase").setTitle("OIEEEE MAEEEEEEE").build()
//    var aps = Aps.builder().setAlert(alert).setSound("default").build()
//    var apns = ApnsConfig.builder().setAps(aps).build()
//    var message = Message.builder().setApnsConfig(apns).setToken("Insert Token").build()
//    FirebaseMessaging.getInstance().send(message)
}