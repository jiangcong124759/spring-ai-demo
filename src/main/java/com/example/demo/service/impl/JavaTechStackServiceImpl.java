package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.JavaTechStackDao;
import com.example.demo.entity.JavaTechStack;
import com.example.demo.service.JavaTechStackService;
import org.springframework.stereotype.Service;

/**
 * (JavaTechStack)表服务实现类
 *
 * @author makejava
 * @since 2026-11-11 23:04:58
 */
@Service("javaTechStackService")
public class JavaTechStackServiceImpl extends ServiceImpl<JavaTechStackDao, JavaTechStack> implements JavaTechStackService {

}

