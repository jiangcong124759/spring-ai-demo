package com.example.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.KnowledgeBases;
import org.apache.ibatis.annotations.Mapper;

/**
 * (KnowledgeBases)表数据库访问层
 *
 * @author makejava
 * @since 2026-03-17 10:12:19
 */
@Mapper
public interface KnowledgeBasesDao extends BaseMapper<KnowledgeBases> {

}

