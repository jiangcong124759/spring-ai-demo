package com.example.demo.tool;


import com.example.demo.entity.JavaTechStack;
import com.example.demo.service.JavaTechStackService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class KnowledgeQueryTools {


    @Autowired
    JavaTechStackService javaTechStackService;

    /**
     * 查询java知识体系
     */
    @Tool(name = "Java知识查询", description = "用于查询Java相关知识")
    public List<String> queryJavaKnowledge() {
        List<JavaTechStack> list = javaTechStackService.list();
        return list.stream()
                .map(JavaTechStack::getName)
                .toList();
    }
}
