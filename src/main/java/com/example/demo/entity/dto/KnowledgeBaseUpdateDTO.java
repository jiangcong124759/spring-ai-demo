package com.example.demo.entity.dto;

import lombok.Data;

/**
 * 知识库更新 DTO
 */
@Data
public class KnowledgeBaseUpdateDTO {

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库描述
     */
    private String description;

}
