package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.MessagesDao;
import com.example.demo.entity.Messages;
import com.example.demo.service.MessagesService;
import org.springframework.stereotype.Service;

/**
 * (Messages)表服务实现类
 *
 * @author makejava
 * @since 2026-03-17 10:12:19
 */
@Service
public class MessagesServiceImpl extends ServiceImpl<MessagesDao, Messages> implements MessagesService {

}

