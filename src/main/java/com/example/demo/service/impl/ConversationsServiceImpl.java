package com.example.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.cloud.ai.memory.redis.RedissonRedisChatMemoryRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.config.RedisChatMemory;
import com.example.demo.dao.ConversationsDao;
import com.example.demo.entity.Conversations;
import com.example.demo.entity.dto.ConversationsDTO;
import com.example.demo.entity.vo.ConversationsVO;
import com.example.demo.entity.vo.MessageVO;
import com.example.demo.service.ConversationsService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

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
}

