package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.KnowledgeFiles;
import com.example.demo.entity.vo.KnowledgeFileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * (KnowledgeFiles)表服务接口
 *
 * @author makejava
 * @since 2026-03-17 10:12:19
 */
public interface KnowledgeFilesService extends IService<KnowledgeFiles> {

    /**
     * 上传文件到知识库
     *
     * @param file 上传的文件
     * @param knowledgeBaseId 知识库ID
     * @return 上传后的文件信息
     */
    KnowledgeFileVO uploadFile(MultipartFile file, String knowledgeBaseId);

    /**
     * 查询文件列表（分页），knowledgeBaseId 为 null 时查询所有文件
     *
     * @param knowledgeBaseId 知识库ID，可为 null
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 文件列表
     */
    List<KnowledgeFileVO> selectFileList(String knowledgeBaseId, int page, int size);

    /**
     * 查询文件总数，knowledgeBaseId 为 null 时查询所有文件
     *
     * @param knowledgeBaseId 知识库ID，可为 null
     * @return 文件总数
     */
    long countFiles(String knowledgeBaseId);
}
