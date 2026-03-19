package com.example.demo.entity.dto;

import com.example.demo.entity.Conversations;
import lombok.Data;


/**
 * (Conversations)表实体类
 *
 * @author makejava
 * @since 2026-03-17 10:12:16
 */
@Data
public class SendMessageDTO {

private String content;

private boolean isStream;

}

