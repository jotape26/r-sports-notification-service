package com.jpapps.notification

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
    fun notifyUsersReservaCreation(reservaID: String) {
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

    fun sendUserReminders(reservaID: String) {
        val firestore = FirestoreClient.getFirestore()
        val reserva = firestore.collection("reservas").document(reservaID)
        val reservaData = reserva.get().get().data
        val jogs = reservaData?.get("jogadores") as ArrayList<Map<String, Any>>
        val date = reservaData.get("dataHora") as Date

        val formatter = SimpleDateFormat("dd/MM")
        val tokens = ArrayList<String>()
        val userCreatorRef = reservaData["primeiroJogador"] as DocumentReference
        val userCreator = userCreatorRef.get().get().data
        val invitationName = userCreator?.get("userName") as String

        jogs.forEach {
            if (it["user"] as? DocumentReference != null) {

                val currentRef = it["user"] as DocumentReference
                val jogadorData = currentRef.get().get()

                val currentUserData = jogadorData.data as Map<String, Any>
                val currentPaymentStatus = it["statusPagamento"] as Boolean


                if (!currentPaymentStatus) {
                    val notifyToken = currentUserData["userNotificationToken"] as? String

                    if (notifyToken != null) {
                        Logger.getGlobal().log(Level.INFO, "Adicionando Token: " + notifyToken)
                        tokens.add(notifyToken)
                    }
                }
            } else {
                //TODO IMPLEMENT SMS FEATURE HERE
            }
        }

        val messageString = invitationName.trim() + " ainda está te esperando para a partida no dia " + formatter.format(date) + ". Acesse agora e veja os detalhes!"
        val alert = ApsAlert.builder().setBody(messageString).setTitle("Lembrete de Partida").setLaunchImage("bola-icon").build()
        val message = MulticastMessage.builder().setApnsConfig(createApnsPush(alert)).addAllTokens(tokens).build()


        Logger.getGlobal().log(Level.INFO, "FirebaseMessage: " + messageString + tokens.toString() )
        FirebaseMessaging.getInstance().sendMulticast(message)
    }

    fun notifyPayment(reservaID: String, userPhone: String) {
        val firestore = FirestoreClient.getFirestore()
        val userPaying = firestore.collection("users").document(userPhone).get().get().data
        if (userPaying != null) {
            val userPayingName = userPaying["userName"] as String
            val reserva = firestore.collection("reservas").document(reservaID)
            val reservaData = reserva.get().get().data
            if (reservaData != null) {
                val userCreatorRef = reservaData["primeiroJogador"] as DocumentReference
                val userCreatorData = userCreatorRef.get().get().data
                if (userCreatorData != null) {
                    val userCreatorToken = userCreatorData["userNotificationToken"] as String

                    val messageString =
                        userPayingName.trim() + " confirmou o pagamento para a reserva #" + reservaID.take(6) + ". Acesse agora e veja os detalhes!"
                    val alert = ApsAlert.builder().setBody(messageString).setTitle("Reserva Atualizada.")
                        .setLaunchImage("bola-icon").build()


                    val message =
                        Message.builder().setApnsConfig(createApnsPush(alert)).setToken(userCreatorToken).build()
                    Logger.getGlobal().log(Level.INFO, "FirebaseMessage: " + messageString + userCreatorToken)
                    FirebaseMessaging.getInstance().send(message)
                }
            }
        }
    }

    fun notifyFullPayment(reserva: DocumentReference) {
        val tokens = ArrayList<String>()
        val reservaData = reserva.get().get().data

        if (reservaData != null) {
            val jogadores = reservaData["jogadores"] as ArrayList<Map<String, Any>>

            jogadores.forEach {
                val jogadorRef = it["user"] as DocumentReference
                val jogadorData = jogadorRef.get().get().data

                if (jogadorData != null) {
                    val notifyToken = jogadorData["userNotificationToken"] as? String

                    if (notifyToken != null) {
                        Logger.getGlobal().log(Level.INFO, "Adicionando Token: " + notifyToken)
                        tokens.add(notifyToken)
                    }
                }
            }
        }

        val messageString = "Reserva #" + reserva.id.take(6) + " confirmada! Agora só resta dividir os times e se preparar pra partida!"
        val alert = ApsAlert.builder().setBody(messageString).setTitle("Partida Confirmada!").setLaunchImage("bola-icon").build()
        val message = MulticastMessage.builder().setApnsConfig(createApnsPush(alert)).addAllTokens(tokens).build()


        Logger.getGlobal().log(Level.INFO, "FirebaseMessage: " + messageString + tokens.toString() )
        FirebaseMessaging.getInstance().sendMulticast(message)
    }

    private fun createApnsPush(alert : ApsAlert): ApnsConfig {
        val aps = Aps.builder().setAlert(alert).setSound("default").setBadge(1).build()
        return ApnsConfig.builder().setAps(aps).build()
    }
}