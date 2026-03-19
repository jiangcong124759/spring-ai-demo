package com.example.demo.controller.api;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.KnowledgeFiles;
import com.example.demo.service.KnowledgeFilesService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * (KnowledgeFiles)表控制层
 *
 * @author makejava
 * @since 2026-03-17 10:12:19
 */
@RestController
@RequestMapping("knowledgeFiles")
public class KnowledgeFilesController  {
    /**
     * 服务对象
     */
    @Resource
    private KnowledgeFilesService knowledgeFilesService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param knowledgeFiles 查询实体
     * @return 所有数据
     */
    @GetMapping
    public void selectAll(Page<KnowledgeFiles> page, KnowledgeFiles knowledgeFiles) {
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
     * @param knowledgeFiles 实体对象
     * @return 新增结果
     */
    @PostMapping
    public void insert(@RequestBody KnowledgeFiles knowledgeFiles) {
    }

    /**
     * 修改数据
     *
     * @param knowledgeFiles 实体对象
     * @return 修改结果
     */
    @PutMapping
    public void update(@RequestBody KnowledgeFiles knowledgeFiles) {
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

