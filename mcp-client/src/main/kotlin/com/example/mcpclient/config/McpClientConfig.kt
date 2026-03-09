package com.example.mcpclient.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class McpClientConfig {

    @Bean
    open fun chatClient(
        builder: ChatClient.Builder,
        toolCallbackProviders: List<ToolCallbackProvider>
    ): ChatClient {
        return builder
            .defaultToolCallbacks(*toolCallbackProviders.flatMap { it.toolCallbacks.toList() }.toTypedArray())
            .build()
    }
}

