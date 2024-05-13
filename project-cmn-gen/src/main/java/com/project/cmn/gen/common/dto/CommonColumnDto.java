package com.project.cmn.gen.common.dto;

import com.project.cmn.gen.common.util.JavaDataType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommonColumnDto {
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
     * 컬럼의 설명
     */
    private String columnComment;

    /**
     * PRI: 기본 키 컬럼
     */
    private String columnKey;

    /**
     * MySQL, MariaDB 용
     * 특정 열에 대해 사용할 수 있는 추가 정보입니다. 이 경우 값은 비어 있지 않습니다:
     * <p>
     * AUTO_INCREMENT 특성이 있는 열의 경우 auto_increment.
     * ON UPDATE CURRENT_TIMESTAMP 특성이 있는 TIMESTAMP 또는 DATETIME 열의 경우 on update CURRENT_TIMESTAMP.
     * 생성된 열의 경우 STORED GENERATED 또는 VIRTUAL GENERATED.
     * 표현식 기본값이 있는 열의 경우 DEFAULT_GENERATED.
     * </p>
     */
    private String extra;

    /**
     * 컬럼명을 Camel Case 로 변경한 이름
     */
    private String fieldName;

    /**
     * Java 로 변경 시 자료형
     */
    private JavaDataType javaDataType;
}
