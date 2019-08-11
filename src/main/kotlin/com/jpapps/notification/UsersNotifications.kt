package com.jpapps.notification

import com.google.cloud.Timestamp
import com.google.cloud.firestore.DocumentReference
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
class UsersNotifications {
    fun notifyUsers(reservaID: String) {
        val firestore = FirestoreClient.getFirestore()
        val reserva = firestore.collection("reservas").document(reservaID)
        val reservaData = reserva.get().get().data
        val jogs = reservaData?.get("jogadores") as ArrayList<Map<String, Any>>
        val date = reservaData.get("dataHora") as Date

        val formatter = SimpleDateFormat("dd/MM")
        val tokens = ArrayList<String>()
        val userCreatorRef = reservaData["primeiroJogador"] as DocumentReference
        val userCreator = userCreatorRef.get().get().data
        val invitationName = userCreator?.get("userName") as? String

        jogs.forEach {
            if (it["user"] as? DocumentReference != null) {

                val currentRef = it["user"] as DocumentReference
                val jogadorData = currentRef.get().get()

                val currentUserData = jogadorData.data as Map<String, Any>
                val notifyToken = currentUserData["userNotificationToken"] as? String

                if (notifyToken != null) {
                    Logger.getGlobal().log(Level.INFO, "Adicionando Token: " + notifyToken )
                    tokens.add(notifyToken)
                }
            } else {
                //TODO IMPLEMENT SMS FEATURE HERE
            }
        }

        var messageString = ""

        if (invitationName != null) {
            messageString = invitationName.trim() + " convidou você para uma partida de futebol no dia " + formatter.format(date) + ". E ai, topa?"
        } else {
            messageString = "Você recebeu um novo convite para jogar futebol, abra o aplicativo para mais detalhes!"
        }

        val alert = ApsAlert.builder().setBody(messageString).setTitle("Partiu jogar?").setLaunchImage("bola-icon").build()

        val message = MulticastMessage.builder().setApnsConfig(createApnsPush(alert)).addAllTokens(tokens).build()


        Logger.getGlobal().log(Level.INFO, "FirebaseMessage: " + messageString + tokens.toString() )
        FirebaseMessaging.getInstance().sendMulticast(message)
    }

    private fun createApnsPush(alert : ApsAlert): ApnsConfig {
        val aps = Aps.builder().setAlert(alert).setSound("default").setBadge(1).build()
        return ApnsConfig.builder().setAps(aps).build()
    }
}