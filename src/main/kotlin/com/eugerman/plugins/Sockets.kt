package com.eugerman.plugins

import com.eugerman.model.Task
import com.eugerman.model.TaskRepository
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.*

fun Application.configureSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val webSocketSessions =
            Collections.synchronizedList<WebSocketServerSession>(arrayListOf())
        webSocket("/tasks") {
            sendAllTasks()
            close(CloseReason(CloseReason.Codes.NORMAL, "All done"))
        }

        webSocket("/tasks/tracked") {
            webSocketSessions.add(this)
            sendAllTasks()

            while (true) {
                val newTask = receiveDeserialized<Task>()
                TaskRepository.add(newTask)
                webSocketSessions.forEach { it.sendSerialized(newTask) }
            }
        }
    }
}

private suspend fun DefaultWebSocketServerSession.sendAllTasks() {
    val tasks = TaskRepository.allTasks()
    for (task in tasks) {
        sendSerialized(task)
        delay(1000)
    }
}
