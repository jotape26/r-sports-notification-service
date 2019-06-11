import java.io.FileInputStream
import com.google.firebase.FirebaseApp
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.DocumentReference
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import io.undertow.Undertow
import io.undertow.client.UndertowClient
import io.undertow.server.HttpHandler
import io.undertow.util.HttpString
import com.relayrides.pushy.apns.ApnsClientBuilder
import com.relayrides.pushy.apns.ApnsClient.DEVELOPMENT_APNS_HOST
import com.relayrides.pushy.apns.ApnsClient
import java.io.File


@Suppress("UNCHECKED_CAST")
class UsersNotifications {
    fun notifyUsers(){

        val serviceAccount = FileInputStream("src/r-sports-backend-firebase-adminsdk-t7d2b-e1908f0145.json")

        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://r-sports-backend.firebaseio.com")
            .build()

        FirebaseApp.initializeApp(options)

        val name = FirestoreClient.getFirestore().collection("reservas").document("uZychHNma1g1SgQaQ97M").get().get().data

        val jogs = name?.get("jogadores") as ArrayList<Any>

        jogs.forEach {
            val test = it as Map<String, Any>
            val user = test["user"] as DocumentReference
            val username = user.get().get().getString("userNotificationToken")
            print(username)

            val server = Undertow.builder()
                .addHttpListener(443, "api.sandbox.push.apple.com")
                .setHandler(HttpHandler { exchange ->
                    exchange.requestHeaders.put(HttpString("path"), "/3/device/" + username)
                    exchange.requestHeaders.put(HttpString("apns-push-type"), "alert")
                    exchange.requestMethod = HttpString("POST")
                    exchange.connection.pushResource("api.sandbox.push.apple.com", HttpString("POST"), exchange.requestHeaders)

                }).build()

            server.start()

        }

        val apnsClient = ApnsClientBuilder()
            .setClientCredentials(File("src/RSports_Push_Sandbox.p12"), "xxxx")
            .build()

//        apnsClient.sendNotification()

    }
}