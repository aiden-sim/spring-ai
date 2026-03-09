package com.example.mcpclient.controller

import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatClient: ChatClient
) {

    @GetMapping
    fun chat(@RequestParam message: String): String {
        return chatClient.prompt()
            .user(message)
            .call()
            .content() ?: "No response"
    }

    @GetMapping("/weather")
    fun weather(@RequestParam city: String): String {
        return chatClient.prompt()
            .user("What is the current weather in $city? Also give me a 3-day forecast.")
            .call()
            .content() ?: "No response"
    }
}

