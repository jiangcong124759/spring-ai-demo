package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.KnowledgeBases;
import com.example.demo.entity.dto.KnowledgeBaseUpdateDTO;
import com.example.demo.entity.vo.KnowledgeBaseVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (KnowledgeBases)表服务接口
 *
 * @author makejava
 * @since 2026-03-17 10:12:19
 */
public interface KnowledgeBasesService extends IService<KnowledgeBases> {

    /**
     * 创建知识库
     * @param knowledgeBases 知识库实体
     * @return 创建后的知识库（包含生成的ID）
     */
    KnowledgeBases createKnowledgeBase(KnowledgeBases knowledgeBases);

    /**
     * 查询知识库列表
     * @return 知识库列表（包含文件数量和分块数统计）
     */
    List<KnowledgeBaseVO> selectKnowledgeBaseList();

    /**
     * 更新知识库
     * @param id 知识库ID
     * @param dto 更新DTO
     * @return 更新后的知识库VO
     */
    KnowledgeBaseVO updateKnowledgeBase(String id, KnowledgeBaseUpdateDTO dto);

    /**
     * 查询知识库详情
     * @param id 知识库ID
     * @return 知识库详情VO
     */
    KnowledgeBaseVO getKnowledgeBaseDetail(String id);

    /**
     * 删除知识库
     * @param id 知识库ID
     */
    void deleteKnowledgeBase(String id);
}

