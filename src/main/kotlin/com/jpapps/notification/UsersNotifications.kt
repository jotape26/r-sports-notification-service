package com.jpapps.notification

import com.google.cloud.firestore.DocumentReference
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.*


@Suppress("UNCHECKED_CAST")
class UsersNotifications {
    fun notifyUsers(){
        val name = FirestoreClient.getFirestore().collection("reservas").document("uZychHNma1g1SgQaQ97M").get().get().data

        val jogs = name?.get("jogadores") as ArrayList<Any>

        var tokens = ArrayList<String>()

        jogs.forEach {
            val test = it as Map<String, Any>
            val user = test["user"] as DocumentReference
            val notificationToken = user.get().get().getString("userNotificationToken")
            checkNotNull(notificationToken)
            tokens.add(notificationToken)

            var alert = ApsAlert.builder().setBody("This push is coming from Heroku and sending to firebase").setTitle("Heroku Push").build()
            var aps = Aps.builder().setAlert(alert).build()
            var apns = ApnsConfig.builder().setAps(aps).build()
            var message = Message.builder().setApnsConfig(apns).build()
            FirebaseMessaging.getInstance().send(message)
        }

//        var message = MulticastMessage.builder().addAllTokens(tokens).putData("Hi man", "alert").build()
//        FirebaseMessaging.getInstance().sendMulticast(message)
    }
}