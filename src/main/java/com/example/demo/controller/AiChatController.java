package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@Tag(name = "AI 聊天", description = "AI 聊天相关接口")
@RestController
@CrossOrigin("*")
public class AiChatController {

    private  ChatClient chatClient;

    @Autowired
    public AiChatController(ChatClient.Builder builder) {
        this.chatClient =  builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }


    /**
     * 文本输出
     * @param input
     * @return
     */
    @Operation(summary = "文本聊天", description = "发送消息并获取 AI 回复的文本内容")
    @GetMapping(value = "/chat",produces= "text/html;charset=utf-8")
    public String chat(
            @Parameter(description = "用户输入的消息", required = true)
            @RequestParam String input) {
        return chatClient.prompt().user(input).call().content();
    }


    /**
     * 流式输出
     * @param input
     * @return
     */
    @Operation(summary = "流式聊天", description = "发送消息并获取 AI 回复的流式内容")
    @GetMapping(value = "/stream",produces= "text/html;charset=utf-8")
    public Flux<String> stream(
            @Parameter(description = "用户输入的消息", required = true)
            @RequestParam String input) {
        return chatClient.prompt().user(input).stream().content();
    }

}
