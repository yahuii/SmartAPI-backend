package com.huiapi.common.model.enums;

import lombok.Getter;

/**
 * @author 顾琴
 */
@Getter
public enum InterfaceInfoStatusEnum {

    ONLINE("上线",1),
    OFFLINE("下线",0);


    private final String text;

    private final int value;


    InterfaceInfoStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

}
