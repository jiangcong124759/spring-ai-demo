package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Tag(name = "AI 聊天记忆", description = "AI 聊天记忆相关接口")
@RestController
@RequestMapping("/memory")
@CrossOrigin("*")
public class AiChatMemoryController {

    private  ChatClient chatClient;

    @Autowired
    public AiChatMemoryController(ChatClient.Builder builder) {
        this.chatClient =  builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
//                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
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
