import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import model.ChatGPTResponse

class ChatGPTPromptService(private val apiKey: String, private val baseUrl: String) {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {ignoreUnknownKeys = true})
        }
        install(Logging) {
            logger = object: Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120000
            socketTimeoutMillis = 120000
        }
    }

    suspend fun getChatCompletion(prompt: String): String {
        val response = httpClient.post(baseUrl) {
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "Bearer $apiKey")
            }
            setBody(ChatCompletionRequest(model = "gpt-4", messages = listOf(ChatMessage(role = "user", content = prompt))))
        }
        val chatResponse = response.body<ChatGPTResponse>()

        return chatResponse.choices[0].message.content
    }

    fun close (){
        httpClient.close()
    }
}

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>
)

@Serializable
data class ChatMessage(val role: String, val content: String)
