package com.example.demo.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 会话视图对象 - 用于返回包含消息列表的完整会话信息
 */
@Data
public class ConversationsVO {

    /**
     * 对话 id
     */
    private String id;

    /**
     * 对话标题
     */
    private String title;

    /**
     * 知识库 id
     */
    private String knowledgeBaseIds;

    /**
     * 消息列表
     */
    private List<MessageVO> messages;

}
