package com.example.demo.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * 知识库视图对象 - 用于返回知识库信息
 */
@Data
public class KnowledgeBaseVO {

    /**
     * 知识库 id
     */
    private String id;

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 文件数量
     */
    private Integer fileCount;

    /**
     * 总分块数
     */
    private Integer totalChunks;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}
