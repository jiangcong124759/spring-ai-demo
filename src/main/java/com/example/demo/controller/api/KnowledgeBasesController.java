package com.example.demo.controller.api;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.KnowledgeBases;
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
@RequestMapping("knowledgeBases")
public class KnowledgeBasesController {
    /**
     * 服务对象
     */
    @Resource
    private KnowledgeBasesService knowledgeBasesService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param knowledgeBases 查询实体
     * @return 所有数据
     */
    @GetMapping
    public void selectAll(Page<KnowledgeBases> page, KnowledgeBases knowledgeBases) {
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public void selectOne(@PathVariable Serializable id) {
    }

    /**
     * 新增数据
     *
     * @param knowledgeBases 实体对象
     * @return 新增结果
     */
    @PostMapping
    public void insert(@RequestBody KnowledgeBases knowledgeBases) {
    }

    /**
     * 修改数据
     *
     * @param knowledgeBases 实体对象
     * @return 修改结果
     */
    @PutMapping
    public void update(@RequestBody KnowledgeBases knowledgeBases) {
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public void delete(@RequestParam("idList") List<Long> idList) {
    }
}

