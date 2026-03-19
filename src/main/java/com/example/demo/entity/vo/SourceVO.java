package com.example.demo.entity.vo;

import lombok.Data;

/**
 * 来源视图对象 - 用于返回消息的引用来源信息
 */
@Data
public class SourceVO {

    /**
     * 文件 ID
     */
    private String fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 分块索引
     */
    private Integer chunkIndex;

    /**
     * 相关度分数
     */
    private Double score;

}
