package com.example.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Messages;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Messages)表数据库访问层
 *
 * @author makejava
 * @since 2026-03-17 10:12:19
 */
@Mapper
public interface MessagesDao extends BaseMapper<Messages> {

}

