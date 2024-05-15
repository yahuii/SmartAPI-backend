package com.huiapi.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @author 顾琴
 * @TableName product
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;


    /**
     * 请求参数
     */
    private String userRequestParams;


    private static final long serialVersionUID = 1L;
}