package com.heldner.crackcapture

import CrackCaptureEntryScreen
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //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
        //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
        //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
        //val chatGptApiClient = ChatGptApiClient("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","https://api.openai.com/v1/chat/completions")

        setContent {
            CrackCaptureEntryScreen()
        }
    }
}
