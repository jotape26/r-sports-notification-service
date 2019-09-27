package com.jpapps.reserva

import com.google.cloud.Timestamp
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
        val criador = (reservaData?.get("primeiroJogador") as DocumentReference)
        val criadorData = criador.get().get().data
        val valorIndividual = reservaData["valorPago"] as Number

        if (reservaData["singlePayer"] as Boolean) {
            var reservasList = criadorData?.get("reservas") as? ArrayList<String>

            if (reservasList.isNullOrEmpty()) {
                reservasList = arrayListOf()
            }
            reservasList.add(reservaID)
            criador.update("reservas", reservasList).get()

        } else {
            val timeID = reservaData["timeID"] as String

            val timeRef = firestore.collection("times").document(timeID)
            val time = timeRef.get().get().data

            val jogadores = time?.get("jogadores") as ArrayList<Map<String, Any>>

            val newUserList = mutableListOf<Map<String, Any>>()

            val notificationTokens = arrayListOf<String>()

            for (i in 0 until jogadores.count()) {
                val currentJogador = jogadores[i]

                if (currentJogador["pendente"] as Boolean) {
                    continue
                } else {
                    val telefone = currentJogador["telefone"] as String

                    val jogadorRef = firestore.collection("users").document(telefone)
                    val jogadorData = jogadorRef.get().get().data

                    if (jogadorData != null) {
                        val currentUserName = jogadorData["userName"] as String

                        var reservasList = jogadorData["reservas"] as? ArrayList<String>

                        if (reservasList.isNullOrEmpty()) {
                            reservasList = arrayListOf()
                        }
                        reservasList.add(reservaID)
                        jogadorRef.update("reservas", reservasList).get()

                        if (telefone == criador.id) {
                            newUserList.add(mapOf("statusPagamento" to true,
                                "user" to jogadorRef,
                                "valorAPagar" to valorIndividual.toDouble(),
                                "userName" to currentUserName))
                        } else {
                            newUserList.add(mapOf("statusPagamento" to false,
                                "user" to jogadorRef,
                                "valorAPagar" to valorIndividual.toDouble(),
                                "userName" to currentUserName))

                            notificationTokens.add(jogadorData["userNotificationToken"] as String)
                        }
                    }
                }
            }

            if (newUserList.isNotEmpty()) {
                reserva.update("jogadores", newUserList).get()
            }
            UsersNotifications().notifyUsersReservaCreation(reservaID, notificationTokens)
        }
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
                    if (!(reservaData["singlePayer"] as Boolean)) {
                        val timeID = reservaData["timeID"] as String

                        val timeRef = firestore.collection("times").document(timeID)
                        val timeData = timeRef.get().get().data
                        var partidas = timeData?.get("partidas") as? ArrayList<Map<String, Any>>

                        if (partidas.isNullOrEmpty()) {
                            partidas = arrayListOf()
                        }

                        partidas.add(mapOf("reserva" to reserva,
                            "quadra" to reservaData["quadraID"] as String,
                            "data" to reservaData["dataHora"] as Timestamp))

                        timeRef.update("partidas", partidas).get()
                    }

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
