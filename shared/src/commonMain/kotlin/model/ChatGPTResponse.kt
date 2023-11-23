package model

import kotlinx.serialization.Serializable

@Serializable
data class ChatGPTResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val usage: Usage
)
