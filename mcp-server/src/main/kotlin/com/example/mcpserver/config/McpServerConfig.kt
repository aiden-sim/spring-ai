package com.example.mcpserver.config

import com.example.mcpserver.tool.WeatherTools
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class McpServerConfig(
    private val weatherTools: WeatherTools
) {

    @Bean
    open fun weatherToolCallbackProvider(): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder()
            .toolObjects(weatherTools)
            .build()
    }
}
