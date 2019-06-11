import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.receiveText
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {

    System.setProperty("Dio.netty.tryReflectionSetAccessible", "true")
    embeddedServer(Netty, 8080) {
        routing {

            get("/") {
                call.respondText("{\"success\" : true }", ContentType.Application.Json)
            }
            post("notifyUsers") {
                val text = call.receiveText()
                call.respondText("{\"success\" : true }", ContentType.Application.Json)

                UsersNotifications().notifyUsers()
            }
        }
    }.start(wait = true)
}