package com.jpapps.reserva

import com.google.cloud.firestore.DocumentReference
import com.google.firebase.cloud.FirestoreClient
import com.jpapps.notification.UsersNotifications
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
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

                val userRef = firestore.collection("users").document(telefone)
                val userData = userRef.get().get()

                if (userData != null) {
                    val currentUserName = userData["userName"] as String

                    var reservasList = userData["reservas"] as? ArrayList<String>

                    if (reservasList.isNullOrEmpty()) {
                        reservasList = arrayListOf()
                    }
                    reservasList.add(reservaID)
                    userRef.update("reservas", reservasList).get()

                    newUserList.add(mapOf("statusPagamento" to false,
                        "user" to userData.reference,
                        "valorAPagar" to valorIndividual,
                        "userName" to currentUserName))
                } else {
                    //TODO SMS NOTIFICATION HERE
                }
            } else {
                newUserList.add(it)
                val userRef = it["user"] as DocumentReference
                val userData = userRef.get().get()
                var reservasList = userData["reservas"] as? ArrayList<String>

                if (reservasList.isNullOrEmpty()) {
                    reservasList = arrayListOf()
                }
                reservasList.add(reservaID)
                userRef.update("reservas", reservasList).get()
            }
        }

        if (newUserList.isNotEmpty()) {
            reserva.update("jogadores", newUserList).get()
        }
        UsersNotifications().notifyUsersReservaCreation(reservaID)
    }

    fun registerPayment(reservaID: String, userPhone: String) {
        val firestore = FirestoreClient.getFirestore()
        val reserva = firestore.collection("reservas").document(reservaID)
        val reservaData = reserva.get().get().data
        val jogs = reservaData?.get("jogadores") as ArrayList<MutableMap<String, Any>>
        val valorPago = reservaData["valorPago"] as Number

        jogs.forEach {
            val userRef = it["user"] as DocumentReference
            if (userPhone == userRef.id) {
                var currentIT = it
                currentIT["statusPagamento"] = true

                val valorAPagar = currentIT["valorAPagar"] as Number
                val valorTotal = valorPago.toDouble() + valorAPagar.toDouble()

                reserva.update("jogadores", jogs).get()
                reserva.update("valorPago", valorTotal).get()

                if (checkIfFullyPaid(reserva)) {
                    UsersNotifications().notifyFullPayment(reserva)
                } else {
                    UsersNotifications().notifyPayment(reservaID, userPhone)
                }
            }
        }
    }

    fun checkIfFullyPaid(reserva: DocumentReference) : Boolean {
        val reservaData = reserva.get().get().data

        if (reservaData != null) {
            val valorPago = reservaData["valorPago"] as Number
            val valorAPagar = reservaData["valorTotal"] as Number

            if (valorAPagar == valorPago) {
                reserva.update("status", "Pago").get()
                return true
            }
            return false
        }
        return false
    }



}
