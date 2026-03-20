package com.example.demo.controller.api;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.KnowledgeBases;
import com.example.demo.entity.MyResult;
import com.example.demo.service.KnowledgeBasesService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * (KnowledgeBases)表控制层
 *
 * @author makejava
 * @since 2026-03-17 10:12:19
 */
@RestController
@RequestMapping("api/v1/knowledge-bases")
public class KnowledgeBasesController {
    /**
     * 服务对象
     */
    @Resource
    private KnowledgeBasesService knowledgeBasesService;

    /**
     * 查询知识库列表
     *
     * @return 知识库列表
     */
    @GetMapping
    public MyResult<java.util.Map<String, Object>> selectAll() {
        java.util.List<com.example.demo.entity.vo.KnowledgeBaseVO> list = knowledgeBasesService.selectKnowledgeBaseList();
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("list", list);
        return MyResult.success(data);
    }

    /**
     * 查询知识库详情
     *
     * @param id 知识库ID
     * @return 知识库详情
     */
    @GetMapping("{id}")
    public MyResult<com.example.demo.entity.vo.KnowledgeBaseVO> getDetail(@PathVariable("id") String id) {
        com.example.demo.entity.vo.KnowledgeBaseVO detail = knowledgeBasesService.getKnowledgeBaseDetail(id);
        return MyResult.success(detail);
    }

    /**
     * 创建知识库
     *
     * @param knowledgeBases 实体对象
     * @return 创建结果
     */
    @PostMapping
    public MyResult<KnowledgeBases> create(@RequestBody KnowledgeBases knowledgeBases) {
        KnowledgeBases created = knowledgeBasesService.createKnowledgeBase(knowledgeBases);
        return MyResult.success(created);
    }

    /**
     * 修改知识库
     *
     * @param id 知识库ID
     * @param dto 更新DTO
     * @return 修改结果
     */
    @PutMapping("{id}")
    public MyResult<com.example.demo.entity.vo.KnowledgeBaseVO> update(
            @PathVariable("id") String id,
            @RequestBody com.example.demo.entity.dto.KnowledgeBaseUpdateDTO dto) {
        com.example.demo.entity.vo.KnowledgeBaseVO updated = knowledgeBasesService.updateKnowledgeBase(id, dto);
        return MyResult.success(updated);
    }

    /**
     * 删除知识库
     *
     * @param id 知识库ID
     * @return 删除结果
     */
    @DeleteMapping("{id}")
    public MyResult<Void> delete(@PathVariable("id") String id) {
        knowledgeBasesService.deleteKnowledgeBase(id);
        return MyResult.success();
    }
}

