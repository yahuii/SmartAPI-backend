package com.huiapiclientsdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 顾琴
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Weather {

    /**
     * 查询的省份
     */
    private String province;

    /**
     * 查询的具体地区（区、县）
     */
    private String place;
}
