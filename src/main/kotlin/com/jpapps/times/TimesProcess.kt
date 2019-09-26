package com.jpapps.times

import com.google.firebase.cloud.FirestoreClient
import com.jpapps.notification.UsersNotifications

@Suppress("UNCHECKED_CAST")
class TimesProcess {
    fun newTeamCreated(timeID: String) {
        val firestore = FirestoreClient.getFirestore()
        val timeRef = firestore.collection("times").document(timeID)
        val timeData = timeRef.get().get().data

        val jogadores = timeData?.get("jogadores") as ArrayList<Map<String, Any>>

        for (i in 0 until jogadores.count()) {
            val it = jogadores[i]

            if (!(it["pendente"] as Boolean)) {
                continue
            }

            val userRef = firestore.collection("users").document(it["telefone"] as String)
            val userData = userRef.get().get().data

            if (!userData.isNullOrEmpty()) {
                val notificationToken = userData["userNotificationToken"] as String
                val timeName = timeData["nome"] as String

                var timesPending = userData["timesTemp"] as? ArrayList<String>
                if (timesPending.isNullOrEmpty()) {
                    timesPending = arrayListOf()
                }
                timesPending.add(timeID)

                userRef.update("timesTemp", timesPending).get()
                UsersNotifications().notifyNewTeam(notificationToken, timeID, timeName)
            }
        }
    }

    fun refuseInvite(timeID: String, user: String) {

        val firestore = FirestoreClient.getFirestore()
        val timeRef = firestore.collection("times").document(timeID)
        val timeData = timeRef.get().get().data

        val jogadoresArr = timeData?.get("jogadores") as ArrayList<Map<String,Any>>
        jogadoresArr.removeIf { (it["telefone"] as String) == user }

        timeRef.update("jogadores", jogadoresArr).get()
    }

    fun acceptInvite(timeID: String, user: String) {

        val firestore = FirestoreClient.getFirestore()
        val timeRef = firestore.collection("times").document(timeID)
        val timeData = timeRef.get().get().data

        val jogadoresArr = timeData?.get("jogadores") as ArrayList<Map<String,Any>>

        val index = jogadoresArr.indexOfFirst {(it["telefone"] as String) == user}
        jogadoresArr.removeAt(index)

        val jogadorRef = firestore.collection("users").document(user)
        val jogadorData = jogadorRef.get().get().data
        val jogadorName = jogadorData?.get("userName") as String

        jogadoresArr.add(index, mapOf<String, Any>("assistsNoTime" to 0,
            "golsNoTime" to 0,
            "nome" to jogadorName,
            "pendente" to false,
            "telefone" to user))

        timeRef.update("jogadores", jogadoresArr).get()
    }
}