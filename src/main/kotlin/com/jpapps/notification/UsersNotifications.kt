package com.jpapps.notification

import com.google.cloud.firestore.DocumentReference
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.*

@Suppress("UNCHECKED_CAST")
class UsersNotifications {
    fun notifyUsers(reservaID: String){
        val name = FirestoreClient.getFirestore().collection("reservas").document(reservaID).get().get().data
        val jogs = name?.get("jogadores") as ArrayList<Any>
        val tokens = ArrayList<String>()
        var invitationName = ""
        jogs.forEach {
            val test = it as Map<String, Any>
            val userRef = test["user"] as DocumentReference
            val user = userRef.get().get()

            val userName = user.getString("userName")
            checkNotNull(userName)
            invitationName = userName

            val notificationToken = user.getString("userRegistrationToken")
            checkNotNull(notificationToken)
            tokens.add(notificationToken)
        }

        val alert = ApsAlert.builder().setBody(invitationName + " convidou você para uma partida de futebol no dia 10/07. E ai, topa?").setTitle("Partiu jogar?").build()

        val message = MulticastMessage.builder().setApnsConfig(createApnsPush(alert)).addAllTokens(tokens).build()
        FirebaseMessaging.getInstance().sendMulticast(message)
    }

    fun createApnsPush(alert : ApsAlert): ApnsConfig {
        val aps = Aps.builder().setAlert(alert).build()
        return ApnsConfig.builder().setAps(aps).build()
    }
}