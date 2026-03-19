package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Conversations;
import com.example.demo.entity.dto.ConversationsDTO;
import com.example.demo.entity.dto.SendMessageDTO;
import com.example.demo.entity.vo.ConversationsVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.Serializable;
import java.util.List;

/**
 * (Conversations)表服务接口
 *
 * @author makejava
 * @since 2026-03-17 10:12:17
 */
public interface ConversationsService extends IService<Conversations> {


    /**
     * 创建一个会话,返回对话id
     * @param dto
     * @return
     */
    ConversationsDTO createConversation(ConversationsDTO dto);


    /**
     * 获取会话详情
     * @param id
     * @return
     */
    ConversationsVO selectConversationInfo(String id);


    /**
     * 发送消息
     * @param id
     * @param dto
     * @return
     */
    Object sendMessage(String id, SendMessageDTO dto);

    /**
     * 流式发送消息 (SSE)
     * @param id 会话ID
     * @param dto 消息DTO
     * @return SseEmitter 流式响应
     */
    SseEmitter sendMessageStream(String id, SendMessageDTO dto);


    /**
     * 获取对话列表
     * @return
     */
    List<ConversationsVO> selectAll();


    /**
     * 删除对话
     * @param id
     */
    void delete(String id);
}

