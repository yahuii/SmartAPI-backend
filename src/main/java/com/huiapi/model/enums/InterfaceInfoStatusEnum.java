package com.huiapi.model.enums;

import lombok.Getter;

/**
 * @author 顾琴
 */
@Getter
public enum InterfaceInfoStatusEnum {

    ONLINE("上线",0),
    OFFLINE("下线",1);


    private final String text;

    private final int value;


    InterfaceInfoStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

}
