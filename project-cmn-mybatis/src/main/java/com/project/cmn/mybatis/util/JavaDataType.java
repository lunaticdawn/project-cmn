package com.project.cmn.mybatis.util;

import lombok.Getter;

public enum JavaDataType {
    STRING("String", "")
    , INTEGER("Integer", "")
    , LONG("Long", "")
    , DOUBLE("Double", "")
    , LOCAL_DATE("LocalDate", "java.time.LocalDate")
    , LOCAL_TIME("LocalTime", "java.time.LocalTime")
    , LOCAL_DATE_TIME("LocalDateTime", "java.time.LocalDateTime");

    @Getter
    final String type;

    @Getter
    final String path;

    JavaDataType(String type, String path) {
        this.type = type;
        this.path = path;
    }
}
