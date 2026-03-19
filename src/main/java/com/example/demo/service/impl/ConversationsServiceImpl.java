package com.example.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.cloud.ai.memory.redis.RedissonRedisChatMemoryRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.config.RedisChatMemory;
import com.example.demo.dao.ConversationsDao;
import com.example.demo.entity.Conversations;
import com.example.demo.entity.dto.ConversationsDTO;
import com.example.demo.entity.dto.SendMessageDTO;
import com.example.demo.entity.vo.ConversationsVO;
import com.example.demo.entity.vo.MessageVO;
import com.example.demo.entity.vo.SourceVO;
import com.example.demo.service.ConversationsService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * (Conversations)表服务实现类
 *
 * @author makejava
 * @since 2026-03-17 10:12:18
 */
@Service
public class ConversationsServiceImpl extends ServiceImpl<ConversationsDao, Conversations> implements ConversationsService {


    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ConversationsDao conversationsDao;

    @Autowired
    private RedisChatMemory redisChatMemory;

    /**
     * 创建一个会话,返回会话Id
     * @param dto
     * @return
     */
    @Override
    public ConversationsDTO createConversation(ConversationsDTO dto) {
        Conversations conversations = new Conversations();
        BeanUtils.copyProperties(dto, conversations);
        this.save(conversations);
        return BeanUtil.toBean(conversations,ConversationsDTO.class);
    }

    @Override
    public ConversationsVO selectConversationInfo(String id) {
        Conversations conversations = this.getById(id);
        if (conversations == null) {
            return null;
        }
        ConversationsVO conversationsVO = BeanUtil.toBean(conversations, ConversationsVO.class);
        //获取会话记录
        List<Message> messages = redisChatMemory.get(id, 5);
        //将 Message 转换为 MessageVO
        List<MessageVO> messageVOList = messages.stream()
                .map(message -> {
                    MessageVO messageVO = new MessageVO();
                    messageVO.setContent(message.getContent());
                    //根据消息类型设置角色
                    if (message instanceof org.springframework.ai.chat.messages.UserMessage) {
                        messageVO.setRole("user");
                    } else if (message instanceof org.springframework.ai.chat.messages.AssistantMessage) {
                        messageVO.setRole("assistant");
                    } else {
                        messageVO.setRole("system");
                    }
                    return messageVO;
                })
                .toList();
        conversationsVO.setMessages(messageVOList);
        return conversationsVO;
    }

    @Override
    public String sendMessage(String id, SendMessageDTO dto) {
        String content = chatClient.prompt().user(dto.getContent()).advisors(
                a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, id)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
        ).call().content();

        return content;
    }

    @Override
    public SseEmitter sendMessageStream(String id, SendMessageDTO dto) {
        SseEmitter emitter = new SseEmitter(0L);
        String messageId = UUID.randomUUID().toString();

        // 在异步线程中处理流式响应
        new Thread(() -> {
            try {
                // 1. 发送 thinking 事件 - 正在检索相关知识
                sendSseEvent(emitter, "thinking", new JSONObject().set("content", "正在检索相关知识..."));

                // 模拟检索延迟
                Thread.sleep(500);

                // 2. 发送 source 事件 - 知识来源（如果有）
                // 这里可以根据实际检索结果发送多个 source
                SourceVO source = new SourceVO();
                source.setFileId("file_001");
                source.setFileName("SpringBoot自动配置详解.pdf");
                source.setScore(0.92);
                sendSseEvent(emitter, "source", new JSONObject().set("data", source));

                // 3. 流式发送 content 事件 - AI 回复内容
                Flux<String> contentFlux = chatClient.prompt()
                        .user(dto.getContent())
                        .advisors(a -> a
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, id)
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                        )
                        .stream()
                        .content();

                StringBuilder fullContent = new StringBuilder();
                contentFlux.subscribe(
                        chunk -> {
                            // 逐字/逐句发送内容
                            sendSseEvent(emitter, "content", new JSONObject().set("content", chunk));
                            fullContent.append(chunk);
                        },
                        error -> {
                            // 发送错误事件
                            sendSseEvent(emitter, "error", new JSONObject().set("message", error.getMessage()));
                            emitter.completeWithError(error);
                        },
                        () -> {
                            // 4. 发送 done 事件 - 完成
                            sendSseEvent(emitter, "done", new JSONObject().set("messageId", messageId));
                            emitter.complete();
                        }
                );

            } catch (Exception e) {
                sendSseEvent(emitter, "error", new JSONObject().set("message", e.getMessage()));
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }


    /**
     * 获取对话列表
     * @return
     */
    @Override
    public List<ConversationsVO> selectAll() {
        List<Conversations> list = this.list();
        return BeanUtil.copyToList(list,ConversationsVO.class);
    }


    /**
     * 删除对话,同时删除会话记录删除会话记录和会话记录中的内容
     * @param id
     */
    @Override
    public void delete(String id) {
        redisChatMemory.clear(id);
        this.removeById(id);
    }

    /**
     * 发送 SSE 事件
     * @param emitter SSE发射器
     * @param type 事件类型
     * @param data 事件数据
     */
    private void sendSseEvent(SseEmitter emitter, String type, JSONObject data) {
        try {
            JSONObject event = new JSONObject();
            event.set("type", type);
            // 将 data 的内容合并到 event 中
            data.forEach((key, value) -> event.set(key, value));
            emitter.send(SseEmitter.event().data(event.toString()));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }
}

