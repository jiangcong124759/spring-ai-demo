package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Conversations;
import com.example.demo.entity.dto.ConversationsDTO;
import com.example.demo.entity.vo.ConversationsVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.io.Serializable;

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
}

