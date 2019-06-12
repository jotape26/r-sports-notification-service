package com.jpapps

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.gson.JsonParser
import com.jpapps.notification.UsersNotifications
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

    val port = System.getenv("PORT")?.toInt() ?: 8080

    val serviceAccount = FileInputStream("src/r-sports-backend-firebase-adminsdk-t7d2b-e1908f0145.json")

    val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://r-sports-backend.firebaseio.com")
        .build()

    FirebaseApp.initializeApp(options)

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
                        UsersNotifications().notifyUsers(documentID)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode(400, "Error"), "No Document ID passed")
                }
            }
        }
    }.start(wait = true)
}