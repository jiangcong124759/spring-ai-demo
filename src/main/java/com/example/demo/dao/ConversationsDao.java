package com.example.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Conversations;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Conversations)表数据库访问层
 *
 * @author makejava
 * @since 2026-03-17 10:12:14
 */
@Mapper
public interface ConversationsDao extends BaseMapper<Conversations> {

}

