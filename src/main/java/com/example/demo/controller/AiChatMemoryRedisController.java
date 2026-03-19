package com.example.demo.controller;

import com.alibaba.cloud.ai.memory.redis.BaseRedisChatMemoryRepository;
import com.example.demo.config.RedisChatMemory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Tag(name = "AI 聊天记忆", description = "AI 聊天记忆相关接口")
@RestController
@RequestMapping("/memory/redis")
public class AiChatMemoryRedisController {

    private final ChatClient chatClient;

    @Autowired
    private final RedisChatMemory chatMemory; // 注入ChatMemory，其实际类型是RedisChatMemory

    @Autowired
    public AiChatMemoryRedisController(ChatClient.Builder builder, RedisChatMemory chatMemory) {
        this.chatClient =  builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
        this.chatMemory = chatMemory;
    }


    /**
     * 流式输出
     * @param prompt
     * @Param chatId
     * @return
     */
    @Operation(summary = "带记忆的流式聊天", description = "发送消息并获取 AI 回复的流式内容，支持对话记忆功能")
    @GetMapping(value = "/stream")
    public Flux<String> stream(
            @Parameter(description = "用户输入的消息", required = true)
            @RequestParam String prompt,
            @Parameter(description = "聊天会话 ID", required = true)
            @RequestParam String chatId,
            HttpServletResponse response) {
        return chatClient.prompt().user(prompt).advisors(
                a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
        ).stream().content();
    }

}