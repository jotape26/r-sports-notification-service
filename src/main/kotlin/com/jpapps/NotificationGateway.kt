package com.jpapps

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.jpapps.notification.UsersNotifications
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.receiveText
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.FileInputStream

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
                call.respondText("{\"success\" : true , \"method\" : \"push\"}", ContentType.Application.Json)
            }
            post("notifyUsers") {
                call.respondText("{\"success\" : true }", ContentType.Application.Json)
                UsersNotifications().notifyUsers(call.receiveText())
            }
        }
    }.start(wait = true)
}