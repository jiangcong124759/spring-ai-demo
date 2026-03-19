package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * (Conversations) 表实体类
 *
 * @author makejava
 * @since 2026-03-17 10:12:16
 */
@SuppressWarnings("serial")
@Data
@TableName("conversations")
public class Conversations implements Serializable {


    /**
     * 对话 id
     */
    @TableId(type = IdType.ASSIGN_UUID)
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
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;


    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;


}


