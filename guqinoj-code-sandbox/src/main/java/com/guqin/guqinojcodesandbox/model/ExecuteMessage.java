package com.guqin.guqinojcodesandbox.model;

import lombok.Data;

/**
 * 执行信息
 * @author 顾琴
 */
@Data
public class ExecuteMessage {

    private Integer exitValue;

    private String message;

    private String errorMessage;

    private Long time;

    private Long memory;
}
