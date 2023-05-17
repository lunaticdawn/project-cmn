package com.project.cmn.mybatis.mssql.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MSSqlColumnDto {
    /**
     * 테이블이 속한 카탈로그의 이름
     */
    private String tableCatalog;

    /**
     * 테이블이 속한 스키마의 이름입니다.
     */
    private String tableSchema;

    /**
     * 테이블 이름
     */
    private String tableName;

    /**
     * 열의 이름
     */
    private String columnName;

    /**
     * 열의 NULL 가능성. 열에 NULL 값을 저장할 수 있으면 YES, 그렇지 않으면 NO입니다.
     */
    private String isNullable;

    /**
     * 열의 자료유형
     * DATA_TYPE 값은 다른 정보 없이 유형 이름만 포함됩니다. COLUMN_TYPE 값에는 유형 이름과 정밀도 또는 길이와 같은 기타 정보가 포함될 수 있습니다.
     */
    private String dataType;

    /**
     * 문자열 열의 경우 최대 길이(문자)입니다.
     */
    private long characterMaximumLength;

    /**
     * 문자열 열의 경우 최대 길이(바이트)입니다.
     */
    private long characterOctetLength;

    /**
     * 숫자 열의 경우 숫자 정밀도입니다. ex) decimal(_precision, _scale)
     */
    private int numericPrecision;

    /**
     * 숫자 열의 경우 숫자 스케일입니다.
     */
    private int numericScale;

    /**
     * 컬럼의 설명
     */
    private String columnComment;

    /**
     * 제약조건 이름
     */
    private String constraintName;
}
