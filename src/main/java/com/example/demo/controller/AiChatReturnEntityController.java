package com.example.demo.controller;

import com.alibaba.dashscope.utils.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@Tag(name = "AI 聊天相关接口返回实体类", description = "AI 聊天相关接口返回实体类")
@RestController
@RequestMapping("/entity")
public class AiChatReturnEntityController {

    private  ChatClient chatClient;

    @Autowired
    public AiChatReturnEntityController(ChatClient.Builder builder) {
        this.chatClient =  builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }


    /**
     * 电影列表响应对象
     * @param actor
     * @param movies
     */
    record ActorFilms(String actor, List<String> movies) {
    }


    /**
     * 文本输出
     * @param input
     * @return
     */
    @Operation(summary = "文本聊天", description = "发送消息并获取 AI 回复的文本内容")
    @GetMapping(value = "/chat")
    public String chat(
            @Parameter(description = "用户输入的消息", required = true) 
            @RequestParam String input) {
        ActorFilms entity = chatClient.prompt().user(input).call().entity(ActorFilms.class);
        return JsonUtils.toJson(entity);
    }


}