package com.jpapps.reserva

import com.google.cloud.firestore.DocumentReference
import com.google.firebase.cloud.FirestoreClient
import com.jpapps.notification.UsersNotifications
import kotlin.collections.ArrayList

class ReservasProcess {

    fun processarReserva(reservaID : String) {
        val firestore = FirestoreClient.getFirestore()
        val reserva = firestore.collection("reservas").document(reservaID)
        val reservaData = reserva.get().get().data
        val jogs = reservaData?.get("jogadores") as ArrayList<Map<String, Any>>
        val valorIndividual = reservaData["valorPago"] as Double

        var newUserList = mutableListOf<Map<String, Any>>()

        jogs.forEach {
            if (it["user"] as? DocumentReference == null) {
                val telefone = it["telefoneTemp"] as String

                val userData = firestore.collection("users").document(telefone).get().get()
                val currentUserName = userData["userName"] as String

                if (userData != null) {
                    newUserList.add(mapOf("statusPagamento" to false,
                        "user" to userData.reference,
                        "valorAPagar" to valorIndividual,
                        "userName" to currentUserName))
                } else {
                    newUserList.add(it)
                }
            } else {
                newUserList.add(it)
            }
        }

        if (newUserList.isNotEmpty()) {
            reserva.update("jogadores", newUserList).get()
        }

        UsersNotifications().notifyUsersReservaCreation(reservaID)
    }



}
