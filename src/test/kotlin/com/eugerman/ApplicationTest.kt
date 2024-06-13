package com.eugerman

import com.eugerman.model.Task
import com.eugerman.model.TaskRepository
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import kotlin.test.Test

class ApplicationTest {
    @Test
    fun testReceiveAllTasks() = testApplication {
        val wsClient = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation.Plugin) {
                json()
            }
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
        val expectedTasks = TaskRepository.allTasks()
        val actualTasks = arrayListOf<Task>()

        wsClient.webSocket("/tasks") {
            incoming.consumeEach {
                val task = converter!!.deserialize<Task>(it, Charsets.UTF_8)
                actualTasks.add(task)
            }
        }

        assertEquals(expectedTasks.size, actualTasks.size)
        expectedTasks.forEachIndexed { index, task ->
            assertEquals(task, actualTasks[index])
        }
    }
}
