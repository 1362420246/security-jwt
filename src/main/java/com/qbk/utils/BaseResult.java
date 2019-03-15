package com.qbk.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 响应结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResult<T> implements Serializable {
    /**
     * 状态码
     */
    private boolean code;
    /**
     * 信息
     */
    private String message;
    /**
     * 数据对象
     */
    private T data;

}
