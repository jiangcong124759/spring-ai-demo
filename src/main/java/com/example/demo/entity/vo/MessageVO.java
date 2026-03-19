package com.example.demo.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 消息视图对象 - 用于返回单条消息的完整信息
 */
@Data
public class MessageVO {

    /**
     * 消息 id
     */
    private String id;

    /**
     * 角色：user 或 assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 来源列表
     */
    private List<SourceVO> sources;

}
