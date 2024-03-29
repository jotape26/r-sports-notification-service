package com.jpapps

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.gson.JsonParser
import com.jpapps.notification.UsersNotifications
import com.jpapps.reserva.ReservasProcess
import com.jpapps.times.TimesProcess
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.receiveText
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.lang.Exception

fun main(args: Array<String>) {
    System.setProperty("Dio.netty.tryReflectionSetAccessible", "true")

    configureFirebase()
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port) {
        routing {
            get("/") {
                call.respondText("{\"success\" : true }", ContentType.Application.Json)
            }
            post("/notifyUsers") {
                try {
                    val params = JsonParser().parse(call.receiveText()).asJsonObject
                    val documentID = params.get("documentID").asString
                    call.respondText("{\"success\" : true , \"method\" : \"push\"}", ContentType.Application.Json)
                    launch {
                        UsersNotifications().sendUserReminders(documentID)
                    }
                } catch (e: Exception) {
                    val message = "{ \"success\" : false, \"message\" : \"No Document ID passed\", \"stackTrace\" : " + e.stackTrace + " }"
                    call.respond(HttpStatusCode(400, "Error"), message)
                }
            }
            post("/processReservation") {
                try {
                    val params = JsonParser().parse(call.receiveText()).asJsonObject
                    val documentID = params.get("documentID").asString
                    call.respondText("{\"success\" : true , \"method\" : \"reservation\"}", ContentType.Application.Json)
                    launch {
                        ReservasProcess().processarReserva(documentID)
                    }
                } catch (e: Exception) {
                    val message = "{ \"success\" : false, \"message\" : \"No Document ID passed\", \"stackTrace\" : " + e.stackTrace + " }"
                    call.respond(HttpStatusCode(400, "Error"), message)
                }
            }
            post("/registerPayment") {
                try {
                    val params = JsonParser().parse(call.receiveText()).asJsonObject
                    val documentID = params.get("documentID").asString
                    val userNumber = params.get("userPhone").asString
                    call.respondText("{\"success\" : true , \"method\" : \"payment\"}", ContentType.Application.Json)
                    launch {
                        ReservasProcess().registerPayment(documentID, userNumber)
                    }
                } catch (e: Exception) {
                    val message = "{ \"success\" : false, \"message\" : \"No Document ID passed\", \"stackTrace\" : " + e.stackTrace + " }"
                    call.respond(HttpStatusCode(400, "Error"), message)
                }
            }
            post("/createNewTeam") {
                try {
                    val params = JsonParser().parse(call.receiveText()).asJsonObject
                    val timeID = params.get("timeID").asString
                    call.respondText("{\"success\" : true , \"method\" : \"createTeam\"}", ContentType.Application.Json)
                    launch {
                        TimesProcess().newTeamCreated(timeID)
                    }
                } catch (e: Exception) {
                    val message = "{ \"success\" : false, \"message\" : \"No Time ID passed\", \"stackTrace\" : " + e.stackTrace + " }"
                    call.respond(HttpStatusCode(400, "Error"), message)
                }
            }
            post("/refuseTeamInvite") {
                try {
                    val params = JsonParser().parse(call.receiveText()).asJsonObject
                    val timeID = params.get("timeID").asString
                    val user = params.get("userPhone").asString
                    launch {
                        TimesProcess().refuseInvite(timeID, user)
                        call.respondText("{\"success\" : true , \"method\" : \"refuseInvite\"}", ContentType.Application.Json)
                    }
                } catch (e: Exception) {
                    val message = "{ \"success\" : false, \"message\" : \"Error, please see stacktrace\", \"stackTrace\" : " + e.stackTrace + " }"
                    call.respond(HttpStatusCode(400, "Error"), message)
                }
            }
            post("/acceptTeamInvite") {
                try {
                    val params = JsonParser().parse(call.receiveText()).asJsonObject
                    val timeID = params.get("timeID").asString
                    val user = params.get("userPhone").asString
                    launch {
                        TimesProcess().acceptInvite(timeID, user)
                        call.respondText("{\"success\" : true , \"method\" : \"acceptInvite\"}", ContentType.Application.Json)
                    }
                } catch (e: Exception) {
                    val message = "{ \"success\" : false, \"message\" : \"Error, please see stacktrace\", \"stackTrace\" : " + e.stackTrace + " }"
                    call.respond(HttpStatusCode(400, "Error"), message)
                }
            }
        }
    }.start(wait = true)
}

fun configureFirebase(){
    val serviceAccount = FileInputStream("src/r-sports-backend-firebase-adminsdk-t7d2b-1737bf42f0.json")
    val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://r-sports-backend.firebaseio.com")
        .build()

    FirebaseApp.initializeApp(options)
}