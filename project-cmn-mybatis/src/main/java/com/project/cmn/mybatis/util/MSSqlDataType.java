package com.project.cmn.mybatis.util;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * MSSql의 Data Type과 맵핑되는 Java Type에 대한 열거형
 */
public enum MSSqlDataType {
    DECIMAL("DECIMAL", JavaDataType.DOUBLE)
    , NUMERIC("NUMERIC", JavaDataType.DOUBLE)
    , FLOAT("FLOAT", JavaDataType.DOUBLE)
    , REAL("REAL", JavaDataType.DOUBLE)
    , INT("INT", JavaDataType.INTEGER)
    , BIGINT("BIGINT", JavaDataType.LONG)
    , SMALLINT("SMALLINT", JavaDataType.INTEGER)
    , TINYINT("TINYINT", JavaDataType.INTEGER)
    , MONEY("MONEY", JavaDataType.DOUBLE)
    , SMALLMONEY("SMALLMONEY", JavaDataType.DOUBLE)
    , DATE("DATE", JavaDataType.LOCAL_DATE)
    , DATETIME("DATETIME", JavaDataType.LOCAL_DATE_TIME)
    , DATETIME2("DATETIME2", JavaDataType.LOCAL_DATE_TIME)
    , SMALLDATETIME("SMALLDATETIME", JavaDataType.LOCAL_DATE_TIME)
    , TIME("TIME", JavaDataType.LOCAL_TIME)
    , BINARY("BINARY", JavaDataType.STRING)
    , VARBINARY("VARBINARY", JavaDataType.STRING)
    , CHAR("CHAR", JavaDataType.STRING)
    , VARCHAR("VARCHAR", JavaDataType.STRING)
    , NCHAR("NCHAR", JavaDataType.STRING)
    , NVARCHAR("NVARCHAR", JavaDataType.STRING)
    , TEXT("TEXT", JavaDataType.STRING)
    , NTEXT("NTEXT", JavaDataType.STRING)
    , IMAGE("IMAGE", JavaDataType.STRING);

    @Getter
    private final String dataType;

    @Getter
    private final JavaDataType javaDataType;

    MSSqlDataType(String dataType, JavaDataType javaDataType) {
        this.dataType = dataType;
        this.javaDataType = javaDataType;
    }

    public static JavaDataType getJavaDataType(String dataType) {
        JavaDataType javaDataType = null;
        MSSqlDataType[] enumList = MSSqlDataType.values();

        for (MSSqlDataType mariaDbDataType : enumList) {
            if (StringUtils.equalsIgnoreCase(mariaDbDataType.dataType, dataType)) {
                javaDataType = mariaDbDataType.javaDataType;
                break;
            }
        }

        return javaDataType;
    }
}
