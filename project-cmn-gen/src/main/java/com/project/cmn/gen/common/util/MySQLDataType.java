package com.project.cmn.gen.common.util;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * MariaDB의 Data Type과 맵핑되는 Java Type에 대한 열거형
 */
@Getter
public enum MySQLDataType {
    TINYINT("TINYINT", JavaDataType.INTEGER)
    , SMALLINT("SMALLINT", JavaDataType.INTEGER)
    , MEDIUMINT("MEDIUMINT", JavaDataType.INTEGER)
    , INT("INT", JavaDataType.INTEGER)
    , BIGINT("BIGINT", JavaDataType.LONG)
    , DECIMAL("DECIMAL", JavaDataType.DOUBLE)
    , FLOAT("FLOAT", JavaDataType.DOUBLE)
    , DOUBLE("DOUBLE", JavaDataType.DOUBLE)
    , DATE("DATE", JavaDataType.LOCAL_DATE)
    , TIME("TIME", JavaDataType.LOCAL_TIME)
    , DATETIME("DATETIME", JavaDataType.LOCAL_DATE_TIME)
    , TIMESTAMP("TIMESTAMP", JavaDataType.LOCAL_DATE_TIME)
    , YEAR("YEAR", JavaDataType.STRING)
    , BINARY("BINARY", JavaDataType.STRING)
    , BLOB("BLOB", JavaDataType.STRING)
    , TEXT("TEXT", JavaDataType.STRING)
    , CHAR("CHAR", JavaDataType.STRING)
    , ENUM("ENUM", JavaDataType.STRING)
    , INET4("INET4", JavaDataType.STRING)
    , INET6("INET6", JavaDataType.STRING)
    , JSON("JSON", JavaDataType.STRING)
    , MEDIUMBLOB("MEDIUMBLOB", JavaDataType.STRING)
    , MEDIUMTEXT("MEDIUMTEXT", JavaDataType.STRING)
    , LONGBLOB("LONGBLOB", JavaDataType.STRING)
    , LONGTEXT("LONGTEXT", JavaDataType.STRING)
    , TINYBLOB("TINYBLOB", JavaDataType.STRING)
    , TINYTEXT("TINYTEXT", JavaDataType.STRING)
    , VARCHAR("VARCHAR", JavaDataType.STRING)
    , UUID("UUID", JavaDataType.STRING);

    private final String dataType;

    private final JavaDataType javaDataType;

    MySQLDataType(String dataType, JavaDataType javaDataType) {
        this.dataType = dataType;
        this.javaDataType = javaDataType;
    }

    public static JavaDataType getJavaDataType(String dataType) {
        JavaDataType javaDataType = null;
        MySQLDataType[] enumList = MySQLDataType.values();

        for (MySQLDataType mariaDbDataType : enumList) {
            if (StringUtils.equalsIgnoreCase(mariaDbDataType.dataType, dataType)) {
                javaDataType = mariaDbDataType.javaDataType;
                break;
            }
        }

        return javaDataType;
    }
}
