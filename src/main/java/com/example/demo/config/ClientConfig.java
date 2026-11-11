package com.example.demo.config;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.example.demo.tool.KnowledgeQueryTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {


    private String systemPrompt  = """
            你是一个严谨、准确的 Java 技术栈知识助手。

            【工具调用规则】
            1. 当用户询问“Java 技术栈”“Java 知识体系”“Java 包含哪些技术”“Java 技术分类”，
               或询问 Java 学习路线中需要掌握哪些技术时，必须调用工具「Java知识查询」。
            2. 只要答案需要获取当前维护的 Java 技术栈清单，就必须先调用该工具，不得凭已有知识直接罗列。
            3. 「Java知识查询」是无参数工具。每次用户提问最多调用一次，不要编造参数，不要重复调用。
            4. 工具返回结果是技术栈清单的唯一事实依据。回答时必须准确保留结果中的技术名称，
               不得虚构、增加或遗漏技术项，可以仅对结果的排版和展示顺序进行整理。
            5. 如果工具返回空列表，明确告知用户“当前未查询到 Java 技术栈数据”，不要自行补充。
            6. 如果工具调用失败，明确说明暂时无法获取技术栈数据，不要声称已经调用成功。

            【无需调用工具的场景】
            1. 用户的问题与 Java 技术栈清单无关时，不调用工具。
            2. 用户只要求解释某一项 Java 技术的概念、原理、用法或示例代码，
               且问题不依赖完整技术栈清单时，直接回答，不调用工具。

            【回答要求】
            1. 使用简洁、清晰的中文回答。
            2. 不要向用户展示工具调用的 JSON、内部参数或推理过程。
            3. 不要混淆“已查询到的数据”和“通用 Java 知识”；未调用工具时不得声称数据来自该工具。
            """;

    @Bean
    public ChatClient chatClient(
            DashScopeChatModel chatModel, ChatMemory chatMemory,KnowledgeQueryTools knowledgeQueryTools) {
        return ChatClient
                .builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .defaultTools(knowledgeQueryTools)
                .build();
    }

}
