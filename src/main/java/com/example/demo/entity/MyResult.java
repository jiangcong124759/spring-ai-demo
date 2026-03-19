package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一响应结果封装类
 *
 * @author admin
 * @since 2026-03-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyResult<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应（无数据）
     */
    public static <T> MyResult<T> success() {
        return new MyResult<>(200, "success", null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> MyResult<T> success(T data) {
        return new MyResult<>(200, "success", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> MyResult<T> success(String message, T data) {
        return new MyResult<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> MyResult<T> error(Integer code, String message) {
        return new MyResult<>(code, message, null);
    }

    /**
     * 失败响应（默认 500）
     */
    public static <T> MyResult<T> error(String message) {
        return new MyResult<>(500, message, null);
    }
}
