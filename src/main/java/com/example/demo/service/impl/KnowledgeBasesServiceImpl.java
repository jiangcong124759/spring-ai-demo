package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.KnowledgeBasesDao;
import com.example.demo.dao.KnowledgeFilesDao;
import com.example.demo.entity.KnowledgeBases;
import com.example.demo.entity.KnowledgeFiles;
import com.example.demo.entity.dto.KnowledgeBaseUpdateDTO;
import com.example.demo.entity.vo.KnowledgeBaseVO;
import com.example.demo.service.KnowledgeBasesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * (KnowledgeBases)表服务实现类
 *
 * @author makejava
 * @since 2026-03-17 10:12:19
 */
@Service
public class KnowledgeBasesServiceImpl extends ServiceImpl<KnowledgeBasesDao, KnowledgeBases> implements KnowledgeBasesService {

    @Autowired
    private KnowledgeFilesDao knowledgeFilesDao;

    @Override
    public KnowledgeBases createKnowledgeBase(KnowledgeBases knowledgeBases) {
        // 生成知识库ID
        knowledgeBases.setId("kb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        // 设置创建时间和更新时间
        Date now = new Date();
        knowledgeBases.setCreatedAt(now);
        knowledgeBases.setUpdatedAt(now);
        // 保存到数据库
        this.save(knowledgeBases);
        return knowledgeBases;
    }

    @Override
    public List<KnowledgeBaseVO> selectKnowledgeBaseList() {
        // 查询所有知识库
        List<KnowledgeBases> knowledgeBasesList = this.list();

        // 转换为 VO 并统计文件数量和分块数
        return knowledgeBasesList.stream().map(kb -> {
            KnowledgeBaseVO vo = new KnowledgeBaseVO();
            vo.setId(kb.getId());
            vo.setName(kb.getName());
            vo.setDescription(kb.getDescription());
            vo.setCreatedAt(kb.getCreatedAt());

            // 统计该知识库下的文件数量和总分块数
            List<KnowledgeFiles> files = knowledgeFilesDao.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<KnowledgeFiles>()
                            .eq(KnowledgeFiles::getKnowledgeBaseId, kb.getId())
            );

            vo.setFileCount(files.size());
            vo.setTotalChunks(files.stream()
                    .mapToInt(file -> file.getChunkCount() != null ? file.getChunkCount() : 0)
                    .sum());

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public KnowledgeBaseVO updateKnowledgeBase(String id, KnowledgeBaseUpdateDTO dto) {
        // 查询知识库
        KnowledgeBases knowledgeBase = this.getById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在: " + id);
        }

        // 更新字段
        if (dto.getName() != null) {
            knowledgeBase.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            knowledgeBase.setDescription(dto.getDescription());
        }
        // 设置更新时间
        knowledgeBase.setUpdatedAt(new Date());

        // 保存到数据库
        this.updateById(knowledgeBase);

        // 转换为 VO 并返回
        return convertToVO(knowledgeBase);
    }

    @Override
    public KnowledgeBaseVO getKnowledgeBaseDetail(String id) {
        // 查询知识库
        KnowledgeBases knowledgeBase = this.getById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在: " + id);
        }

        // 转换为 VO 并返回
        return convertToVO(knowledgeBase);
    }

    @Override
    public void deleteKnowledgeBase(String id) {
        // 查询知识库
        KnowledgeBases knowledgeBase = this.getById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在: " + id);
        }

        // 删除知识库下的所有文件记录
        knowledgeFilesDao.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<KnowledgeFiles>()
                        .eq(KnowledgeFiles::getKnowledgeBaseId, id)
        );

        // 删除知识库
        this.removeById(id);
    }

    /**
     * 将 KnowledgeBases 转换为 KnowledgeBaseVO
     */
    private KnowledgeBaseVO convertToVO(KnowledgeBases kb) {
        KnowledgeBaseVO vo = new KnowledgeBaseVO();
        vo.setId(kb.getId());
        vo.setName(kb.getName());
        vo.setDescription(kb.getDescription());
        vo.setCreatedAt(kb.getCreatedAt());
        vo.setUpdatedAt(kb.getUpdatedAt());

        // 统计该知识库下的文件数量和总分块数
        List<KnowledgeFiles> files = knowledgeFilesDao.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<KnowledgeFiles>()
                        .eq(KnowledgeFiles::getKnowledgeBaseId, kb.getId())
        );

        vo.setFileCount(files.size());
        vo.setTotalChunks(files.stream()
                .mapToInt(file -> file.getChunkCount() != null ? file.getChunkCount() : 0)
                .sum());

        return vo;
    }
}

