package com.project.cmn.gen.common.util;

import lombok.Getter;

/**
 * Java 타입에 따른 Import 정보
 */
@Getter
public enum JavaDataType {
    STRING("String", "")
    , INTEGER("Integer", "")
    , LONG("Long", "")
    , DOUBLE("Double", "")
    , LOCAL_DATE("LocalDate", "java.time.LocalDate")
    , LOCAL_TIME("LocalTime", "java.time.LocalTime")
    , LOCAL_DATE_TIME("LocalDateTime", "java.time.LocalDateTime");

    final String type;

    final String path;

    JavaDataType(String type, String path) {
        this.type = type;
        this.path = path;
    }
}
