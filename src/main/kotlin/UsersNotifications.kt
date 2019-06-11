import java.io.FileInputStream
import com.google.firebase.FirebaseApp
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.DocumentReference
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage


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

        var tokens = ArrayList<String>()

        jogs.forEach {
            val test = it as Map<String, Any>
            val user = test["user"] as DocumentReference
            val notificationToken = user.get().get().getString("userNotificationToken")
            checkNotNull(notificationToken)
            tokens.add(notificationToken)
        }

        var message = MulticastMessage.builder().addAllTokens(tokens).putData("Hi man", "alert").build()

        FirebaseMessaging.getInstance().sendMulticast(message)
    }
}