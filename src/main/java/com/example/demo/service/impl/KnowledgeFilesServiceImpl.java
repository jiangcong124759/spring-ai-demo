package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.KnowledgeBasesDao;
import com.example.demo.dao.KnowledgeFilesDao;
import com.example.demo.entity.KnowledgeBases;
import com.example.demo.entity.KnowledgeFiles;
import com.example.demo.entity.vo.KnowledgeFileVO;
import com.example.demo.service.KnowledgeFilesService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * (KnowledgeFiles)表服务实现类
 *
 * @author makejava
 * @since 2026-03-17 10:12:19
 */
@Service
public class KnowledgeFilesServiceImpl extends ServiceImpl<KnowledgeFilesDao, KnowledgeFiles> implements KnowledgeFilesService {

    @Autowired
    private KnowledgeBasesDao knowledgeBasesDao;

    /**
     * 文件存储路径，从配置读取，默认为项目目录下的 uploads
     */
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private Path uploadPath;

    @Override
    public KnowledgeFileVO uploadFile(MultipartFile file, String knowledgeBaseId) {
        // 1. 校验知识库是否存在
        KnowledgeBases knowledgeBase = knowledgeBasesDao.selectById(knowledgeBaseId);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在: " + knowledgeBaseId);
        }

        // 2. 生成文件ID和存储路径
        String fileId = "file_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        String fileName = fileId + (fileExtension.isEmpty() ? "" : "." + fileExtension);
        File destFile = uploadPath.resolve(fileName).toFile();
        File parentDir = destFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败: " + e.getMessage(), e);
        }

        // 4. 创建文件记录
        KnowledgeFiles knowledgeFile = new KnowledgeFiles();
        knowledgeFile.setId(fileId);
        knowledgeFile.setName(originalFilename);
        knowledgeFile.setType(fileExtension.toLowerCase());
        knowledgeFile.setSize(file.getSize());
        knowledgeFile.setKnowledgeBaseId(knowledgeBaseId);
        knowledgeFile.setStatus("uploaded"); // 已上传，待索引
        knowledgeFile.setChunkCount(0); // 初始分块数为0
        knowledgeFile.setStoragePath(destFile.getAbsolutePath());

        Date now = new Date();
        knowledgeFile.setCreatedAt(now);
        knowledgeFile.setUpdatedAt(now);

        // 5. 保存到数据库
        this.save(knowledgeFile);

        // 6. 转换为VO并返回
        return convertToVO(knowledgeFile, knowledgeBase.getName());
    }

    @Override
    public List<KnowledgeFileVO> selectFileList(String knowledgeBaseId, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;

        // 构建查询条件
        LambdaQueryWrapper<KnowledgeFiles> wrapper = new LambdaQueryWrapper<KnowledgeFiles>()
                .orderByDesc(KnowledgeFiles::getCreatedAt)
                .last("LIMIT " + size + " OFFSET " + offset);

        // 如果指定了知识库ID，添加过滤条件
        if (knowledgeBaseId != null && !knowledgeBaseId.isEmpty()) {
            wrapper.eq(KnowledgeFiles::getKnowledgeBaseId, knowledgeBaseId);
        }

        // 查询分页文件列表
        List<KnowledgeFiles> files = this.list(wrapper);

        // 转换为VO列表
        return files.stream()
                .map(file -> {
                    // 查询每个文件对应的知识库名称
                    KnowledgeBases kb = knowledgeBasesDao.selectById(file.getKnowledgeBaseId());
                    String kbName = kb != null ? kb.getName() : "";
                    return convertToVO(file, kbName);
                })
                .collect(Collectors.toList());
    }

    @Override
    public long countFiles(String knowledgeBaseId) {
        LambdaQueryWrapper<KnowledgeFiles> wrapper = new LambdaQueryWrapper<>();

        // 如果指定了知识库ID，添加过滤条件
        if (knowledgeBaseId != null && !knowledgeBaseId.isEmpty()) {
            wrapper.eq(KnowledgeFiles::getKnowledgeBaseId, knowledgeBaseId);
        }

        return this.count(wrapper);
    }

    /**
     * 初始化上传目录
     */
    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        File dir = uploadPath.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 将 KnowledgeFiles 转换为 KnowledgeFileVO
     */
    private KnowledgeFileVO convertToVO(KnowledgeFiles file, String knowledgeBaseName) {
        KnowledgeFileVO vo = new KnowledgeFileVO();
        vo.setId(file.getId());
        vo.setName(file.getName());
        vo.setType(file.getType());
        vo.setSize(file.getSize());
        vo.setKnowledgeBaseId(file.getKnowledgeBaseId());
        vo.setKnowledgeBaseName(knowledgeBaseName);
        vo.setStatus(file.getStatus());
        vo.setChunkCount(file.getChunkCount());
        vo.setCreatedAt(file.getCreatedAt());
        vo.setUpdatedAt(file.getUpdatedAt());
        return vo;
    }
}
