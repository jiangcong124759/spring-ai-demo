package com.example.demo.controller.api;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Conversations;
import com.example.demo.entity.MyResult;
import com.example.demo.entity.dto.ConversationsDTO;
import com.example.demo.entity.dto.SendMessageDTO;
import com.example.demo.entity.vo.ConversationsVO;
import com.example.demo.service.ConversationsService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.Serializable;
import java.util.List;

/**
 * (Conversations)表控制层
 *
 * @author makejava
 * @since 2026-03-17 10:12:14
 */
@RestController
@RequestMapping("api/v1/conversations")
@CrossOrigin("*")
public class ConversationsController  {
    /**
     * 服务对象
     */
    @Resource
    private ConversationsService conversationsService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param conversations 查询实体
     * @return 所有数据
     */
    @GetMapping
    public MyResult<List<ConversationsVO>> selectAll() {
        return MyResult.success(conversationsService.selectAll());
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("getByConversationId/{id}")
    public MyResult<ConversationsVO> selectOne(@PathVariable("id") String id) {
        return MyResult.success(conversationsService.selectConversationInfo(id));
    }

    /**
     * 新增数据
     * @return 新增结果
     */
    @PostMapping("create")
    public MyResult insert(@RequestBody ConversationsDTO dto) {
        return MyResult.success(conversationsService.createConversation(dto));
    }

//
//    /**
//     * 对话 - 非流式
//     */
//    @PostMapping("{id}/messages")
//    public MyResult sendMessage(@PathVariable("id") String id,@RequestBody SendMessageDTO dto) {
//        return MyResult.success(conversationsService.sendMessage(id,dto));
//    }

    /**
     * 对话 - 流式 (SSE)
     * 根据 dto.isStream 判断是否使用流式响应
     */
    @PostMapping(value = "{id}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessageStream(@PathVariable("id") String id, @RequestBody SendMessageDTO dto) {
        return conversationsService.sendMessageStream(id, dto);
    }



    /**
     * 修改数据
     *
     * @param conversations 实体对象
     * @return 修改结果
     */
    @PutMapping
    public void update(@RequestBody Conversations conversations) {

    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping("{id}")
    public MyResult delete(@PathVariable("id") String id) {
        conversationsService.delete(id);
        return MyResult.success();
    }
}

