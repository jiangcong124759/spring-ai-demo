package com.example.demo.controller.api;

import com.example.demo.entity.MyResult;
import com.example.demo.entity.vo.KnowledgeFileVO;
import com.example.demo.service.KnowledgeFilesService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (KnowledgeFiles)表控制层
 *
 * @author makejava
 * @since 2026-03-17 10:12:19
 */
@RestController
@RequestMapping("/api/v1/files")
public class KnowledgeFilesController {

    @Resource
    private KnowledgeFilesService knowledgeFilesService;

    /**
     * 查询文件列表（分页），不传 knowledgeBaseId 时查询所有文件
     *
     * @param knowledgeBaseId 知识库ID，可选
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 文件列表
     */
    @GetMapping
    public MyResult<Map<String, Object>> selectAll(
            @RequestParam(value = "knowledgeBaseId", required = false) String knowledgeBaseId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        // 参数校验
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100; // 最大每页100条
        }

        List<KnowledgeFileVO> list = knowledgeFilesService.selectFileList(knowledgeBaseId, page, size);
        long total = knowledgeFilesService.countFiles(knowledgeBaseId);

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("list", list);
        data.put("page", page);
        data.put("size", size);
        return MyResult.success(data);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public void selectOne(@PathVariable String id) {
    }

    /**
     * 上传文件到知识库
     *
     * @param file 上传的文件
     * @param knowledgeBaseId 知识库ID
     * @return 上传结果
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MyResult<KnowledgeFileVO> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("knowledgeBaseId") String knowledgeBaseId
    ) {
        KnowledgeFileVO uploadedFile = knowledgeFilesService.uploadFile(file, knowledgeBaseId);
        return MyResult.success(uploadedFile);
    }

    /**
     * 修改数据
     *
     * @param knowledgeFiles 实体对象
     * @return 修改结果
     */
    @PutMapping
    public void update(@RequestBody Object knowledgeFiles) {
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public void delete(@RequestParam("idList") List<String> idList) {
    }
}
