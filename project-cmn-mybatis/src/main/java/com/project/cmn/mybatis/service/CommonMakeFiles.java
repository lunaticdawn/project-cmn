package com.project.cmn.mybatis.service;

import com.project.cmn.mybatis.dto.CommonColumnDto;
import com.project.cmn.mybatis.dto.FileInfoDto;
import com.project.cmn.mybatis.util.JavaDataType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import java.util.List;

public class CommonMakeFiles {
    private static final String BLANK_4 = "    ";

    private enum FieldPosition {
        SELECT_FIELD
        , INSERT_FIELD
        , INSERT_VALUE
        , UPDATE_SET
        , WHERE;

        FieldPosition() {
        }
    }

    /**
     * Dto 파일 내용
     *
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param columnsList 테이블의 컬럼 리스트
     * @return Dto 파일 내용
     */
    protected String getDtoContent(FileInfoDto fileInfoDto, List<CommonColumnDto> columnsList) {
        boolean isLocalDate = false;
        boolean isLocalTime = false;
        boolean isLocalDateTime = false;

        for (CommonColumnDto commonColumnDto : columnsList) {
            // java.time.LocalDate 타입이 있는지 체크
            if (commonColumnDto.getJavaDataType() == JavaDataType.LOCAL_DATE && !isLocalDate) {
                isLocalDate = true;
            }

            // java.time.LocalTime 타입이 있는지 체크
            if (commonColumnDto.getJavaDataType() == JavaDataType.LOCAL_TIME && !isLocalTime) {
                isLocalTime = true;
            }

            // java.time.LocalDateTime 타입이 있는지 체크
            if (commonColumnDto.getJavaDataType() == JavaDataType.LOCAL_DATE_TIME && !isLocalDateTime) {
                isLocalDateTime = true;
            }
        }

        String importStr = "import ";
        StringBuilder buff = new StringBuilder();

        // Dto 생성
        buff.append("package ").append(fileInfoDto.getDtoPackage()).append(";\n");
        buff.append("\n");

        if (isLocalDate) {
            buff.append(importStr).append(JavaDataType.LOCAL_DATE.getPath()).append(";").append("\n");
        }

        if (isLocalTime) {
            buff.append(importStr).append(JavaDataType.LOCAL_TIME.getPath()).append(";").append("\n");
        }

        if (isLocalDateTime) {
            buff.append(importStr).append(JavaDataType.LOCAL_DATE_TIME.getPath()).append(";").append("\n");
        }

        buff.append("import com.fasterxml.jackson.annotation.JsonProperty;").append("\n");
        buff.append("import lombok.Getter;").append("\n");
        buff.append("import lombok.Setter;").append("\n");
        buff.append("import lombok.ToString;").append("\n");
        buff.append("\n");
        buff.append("/**").append("\n");
        buff.append(" * ").append(columnsList.get(0).getTableSchema()).append(".").append(columnsList.get(0).getTableName()).append("\n");
        buff.append(" */").append("\n");
        buff.append("@Getter").append("\n");
        buff.append("@Setter").append("\n");
        buff.append("@ToString").append("\n");
        buff.append("public class ").append(fileInfoDto.getDtoFilename()).append(" {").append("\n");

        for (CommonColumnDto commonColumnDto : columnsList) {
            buff.append("    /**").append("\n");
            buff.append("     * ").append(commonColumnDto.getColumnComment()).append("\n");
            buff.append("     */").append("\n");
            buff.append("    @JsonProperty(\"").append(commonColumnDto.getColumnName().toLowerCase()).append("\")").append("\n");
            buff.append("    private ").append(commonColumnDto.getJavaDataType().getType()).append(" ").append(CaseUtils.toCamelCase(commonColumnDto.getColumnName(), false, '_')).append(";").append("\n");
            buff.append("\n");
        }

        buff.append("}");

        return buff.toString();
    }

    /**
     * Mapper 파일의 내용을 만들어 반환한다.
     *
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param columnsList 테이블의 컬럼 리스트
     * @return Mapper 파일의 내용
     */
    protected String getMapperContent(FileInfoDto fileInfoDto, List<CommonColumnDto> columnsList) {
        StringBuilder builder = new StringBuilder();

        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
        builder.append("<mapper namespace=\"").append(fileInfoDto.getMapperNamespace()).append("\">\n");

        String tableName = columnsList.get(0).getTableName().toUpperCase();

        // Select 문
        builder.append(this.getSelectStatement(fileInfoDto, columnsList, tableName)).append("\n");

        // Insert 문
        builder.append(this.getInsertStatement(fileInfoDto, columnsList, tableName)).append("\n");

        // Update 문
        builder.append(this.getUpdateStatement(fileInfoDto, columnsList, tableName));

        builder.append("</mapper>");

        return builder.toString();
    }

    /**
     * Select 문의 내용을 만들어 반환한다.
     *
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param columnsList 테이블의 컬럼 리스트
     * @param tableName 테이블 이름
     * @return Select 문의 내용
     */
    private String getSelectStatement(FileInfoDto fileInfoDto, List<CommonColumnDto> columnsList, String tableName) {
        boolean isFirst = true;

        StringBuilder builder = new StringBuilder();

        builder.append(BLANK_4).append(BLANK_4).append("SELECT\n");

        for (CommonColumnDto commonColumnDto : columnsList) {
            builder.append(this.getFieldClause(FieldPosition.SELECT_FIELD, isFirst, commonColumnDto));

            if (isFirst) {
                isFirst = false;
            }
        }

        builder.append(BLANK_4).append(BLANK_4).append("FROM\n");
        builder.append(BLANK_4).append(BLANK_4).append(BLANK_4).append(tableName).append("\n");
        builder.append(BLANK_4).append(BLANK_4).append("WHERE\n");
        builder.append(this.getWhereClause("S", columnsList));

        return this.wrapQuery("select", fileInfoDto, builder.toString());
    }

    /**
     * Insert 문의 내용을 만들어 반환한다.
     *
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param columnsList 테이블의 컬럼 리스트
     * @param tableName 테이블 이름
     * @return Insert 문의 내용
     */
    private String getInsertStatement(FileInfoDto fileInfoDto, List<CommonColumnDto> columnsList, String tableName) {
        boolean isFirst = true;

        StringBuilder builder = new StringBuilder();

        builder.append(BLANK_4).append(BLANK_4).append("INSERT INTO ").append(tableName).append(" (\n");

        for (CommonColumnDto commonColumnDto : columnsList) {
            // auto_increment 인 컬럼은 skip
            if (StringUtils.equals(commonColumnDto.getExtra(), "auto_increment")) {
                continue;
            }

            builder.append(this.getFieldClause(FieldPosition.INSERT_FIELD, isFirst, commonColumnDto));

            if (isFirst) {
                isFirst = false;
            }
        }

        builder.append(BLANK_4).append(BLANK_4).append(") VALUES (\n");

        isFirst = true;

        for (CommonColumnDto commonColumnDto : columnsList) {
            // auto_increment 인 컬럼은 skip
            if (StringUtils.equals(commonColumnDto.getExtra(), "auto_increment")) {
                continue;
            }

            builder.append(this.getFieldClause(FieldPosition.INSERT_VALUE, isFirst, commonColumnDto));

            if (isFirst) {
                isFirst = false;
            }
        }

        builder.append(BLANK_4).append(BLANK_4).append(")\n");

        return this.wrapQuery("insert", fileInfoDto, builder.toString());
    }

    /**
     * Update 문의 내용을 만들어 반환한다.
     *
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param columnsList 테이블의 컬럼 리스트
     * @param tableName 테이블 이름
     * @return Update 문의 내용
     */
    private String getUpdateStatement(FileInfoDto fileInfoDto, List<CommonColumnDto> columnsList, String tableName) {
        boolean isFirst = true;

        StringBuilder builder = new StringBuilder();

        builder.append(BLANK_4).append(BLANK_4).append("UPDATE ").append(tableName).append("\n");
        builder.append(BLANK_4).append(BLANK_4).append("<set>\n");

        for (CommonColumnDto commonColumnDto : columnsList) {
            if (StringUtils.equals(commonColumnDto.getColumnKey(), "PRI")) {
                continue;
            }

            builder.append(this.getFieldClause(FieldPosition.UPDATE_SET, isFirst, commonColumnDto));

            if (isFirst) {
                isFirst = false;
            }
        }

        builder.append(BLANK_4).append(BLANK_4).append("</set>\n");
        builder.append(BLANK_4).append(BLANK_4).append("WHERE\n");
        builder.append(this.getWhereClause("U", columnsList));

        return this.wrapQuery("update", fileInfoDto, builder.toString());
    }

    /**
     * 쿼리를 MyBatis 의 태그로 감싼 후 반환한다.
     *
     * @param type select, insert, update
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param query 쿼리
     * @return MyBatis 의 태그로 감싼 쿼리
     */
    private String wrapQuery(String type, FileInfoDto fileInfoDto, String query) {
        StringBuilder builder = new StringBuilder();

        builder.append(BLANK_4).append("<").append(type).append(" id=\"").append(type).append(fileInfoDto.getBasicFilename()).append("\"");

        if (StringUtils.equals(type, "select")) {
            builder.append(" resultType=\"").append(fileInfoDto.getDtoPackage()).append(".").append(fileInfoDto.getDtoFilename()).append("\"");
        }

        builder.append(">\n");

        builder.append(BLANK_4).append(BLANK_4).append("/* ").append(fileInfoDto.getMapperNamespace()).append(".").append(type).append(fileInfoDto.getBasicFilename()).append(" */\n");
        builder.append(query);
        builder.append(BLANK_4).append("</").append(type).append(">\n");

        return builder.toString();
    }

    /**
     * Where 절을 만들어 반환한다.
     *
     * @param queryType S: select, U: update
     * @param columnsList 테이블의 컬럼 리스트
     * @return Where 절
     */
    private String getWhereClause(String queryType, List<CommonColumnDto> columnsList) {
        boolean isFisrt = true;
        StringBuilder builder = new StringBuilder();

        for (CommonColumnDto commonColumnDto : columnsList) {
            if (StringUtils.equals(queryType, "S")) {
                if (StringUtils.equals(commonColumnDto.getIsNullable(), "NO")
                        && commonColumnDto.getJavaDataType() != JavaDataType.LOCAL_DATE_TIME
                        && commonColumnDto.getJavaDataType() != JavaDataType.LOCAL_DATE
                        && commonColumnDto.getJavaDataType() != JavaDataType.LOCAL_TIME) {
                    builder.append(this.getFieldClause(FieldPosition.WHERE, isFisrt, commonColumnDto));
                }
            } else {
                if (StringUtils.equals(commonColumnDto.getColumnKey(), "PRI")) {
                    builder.append(this.getFieldClause(FieldPosition.WHERE, isFisrt, commonColumnDto));
                }
            }

            if (isFisrt) {
                isFisrt = false;
            }
        }

        return builder.toString();
    }

    /**
     * 위치 별로 필드에 대한 쿼리문을 가져온다.
     *
     * @param fieldPosition {@link FieldPosition} 필드의 위치
     * @param isFirst 첫 필드 여부
     * @param commonColumnDto 필드 정보
     * @return 필드에 대한 쿼리문
     */
    private String getFieldClause(FieldPosition fieldPosition, boolean isFirst, CommonColumnDto commonColumnDto) {
        if (StringUtils.equals(commonColumnDto.getIsNullable(), "NO")
                || fieldPosition == FieldPosition.SELECT_FIELD) {
            return this.getNotNullField(fieldPosition, isFirst, commonColumnDto) + "\n";
        } else {
            return this.getNullableField(fieldPosition, isFirst, commonColumnDto) + "\n";
        }
    }

    /**
     * Not Null 필드에 대한 쿼리문을 가져온다.
     *
     * @param fieldPosition {@link FieldPosition} 필드의 위치
     * @param isFirst 첫 필드 여부
     * @param commonColumnDto 필드 정보
     * @return 필드에 대한 쿼리문
     */
    private String getNotNullField(FieldPosition fieldPosition, boolean isFirst, CommonColumnDto commonColumnDto) {
        StringBuilder builder = new StringBuilder();

        if (fieldPosition == FieldPosition.SELECT_FIELD
                || fieldPosition == FieldPosition.INSERT_FIELD) {
            builder.append(BLANK_4).append(BLANK_4).append(BLANK_4);

            if (!isFirst) {
                builder.append(", ");
            }

            builder.append(commonColumnDto.getColumnName());
        }

        if (fieldPosition == FieldPosition.INSERT_VALUE) {
            builder.append(BLANK_4).append(BLANK_4).append(BLANK_4);

            if (!isFirst) {
                builder.append(", ");
            }

            builder.append(this.getValueWord(commonColumnDto));
        }

        if (fieldPosition == FieldPosition.UPDATE_SET) {
            builder.append(this.getConditionClause(fieldPosition, commonColumnDto));
        }

        if (fieldPosition == FieldPosition.WHERE) {
            if (StringUtils.equals(commonColumnDto.getColumnKey(), "PRI")) {
                builder.append(BLANK_4).append(BLANK_4).append(BLANK_4);

                if (!isFirst) {
                    builder.append("AND ");
                }

                builder.append(commonColumnDto.getColumnName()).append(" = ").append(this.getValueWord(commonColumnDto));
            } else {
                builder.append(this.getConditionClause(fieldPosition, commonColumnDto));
            }
        }

        return builder.toString();
    }

    /**
     * Null 필드에 대한 쿼리문을 가져온다.
     *
     * @param fieldPosition {@link FieldPosition} 필드의 위치
     * @param isFirst 첫 필드 여부
     * @param commonColumnDto 필드 정보
     * @return 필드에 대한 쿼리문
     */
    private String getNullableField(FieldPosition fieldPosition, boolean isFirst, CommonColumnDto commonColumnDto) {
        StringBuilder builder = new StringBuilder();

        if (fieldPosition == FieldPosition.INSERT_FIELD
                || fieldPosition == FieldPosition.INSERT_VALUE) {
            builder.append(this.getConditionClause(fieldPosition, commonColumnDto));
        }

        if (fieldPosition == FieldPosition.UPDATE_SET) {
            builder.append(BLANK_4).append(BLANK_4).append(BLANK_4);

            if (!isFirst) {
                builder.append(", ");
            }

            builder.append(commonColumnDto.getColumnName()).append(" = ").append(this.getValueWord(commonColumnDto));
        }

        return builder.toString();
    }

    /**
     * 필드에 대한 조건문 구문을 만들어 반환한다.
     *
     * @param fieldPosition {@link FieldPosition} 필드의 위치
     * @param commonColumnDto 컬럼 정보
     * @return 필드에 대한 조건문
     */
    private String getConditionClause(FieldPosition fieldPosition, CommonColumnDto commonColumnDto) {
        StringBuilder builder = new StringBuilder();

        if (commonColumnDto.getJavaDataType() == JavaDataType.STRING) {
            builder.append(BLANK_4).append(BLANK_4).append(BLANK_4).append("<if test=\"@org.apache.commons.lang3.StringUtils@isNotBlank(").append(commonColumnDto.getFieldName()).append(")\">\n");
        } else {
            builder.append(BLANK_4).append(BLANK_4).append(BLANK_4).append("<if test=\"").append(commonColumnDto.getFieldName()).append(" != null").append("\">\n");
        }

        if (fieldPosition == FieldPosition.INSERT_FIELD) {
            builder.append(BLANK_4).append(BLANK_4).append(BLANK_4).append(BLANK_4).append(", ").append(commonColumnDto.getColumnName()).append("\n");
        }

        if (fieldPosition == FieldPosition.INSERT_VALUE) {
            builder.append(BLANK_4).append(BLANK_4).append(BLANK_4).append(BLANK_4).append(", ").append("#{").append(commonColumnDto.getFieldName()).append("}").append("\n");
        }

        if (fieldPosition == FieldPosition.UPDATE_SET
                || fieldPosition == FieldPosition.WHERE) {
            builder.append(BLANK_4).append(BLANK_4).append(BLANK_4).append(BLANK_4);

            if (fieldPosition == FieldPosition.UPDATE_SET) {
                builder.append(", ");
            } else {
                builder.append("AND ");
            }

            builder.append(commonColumnDto.getColumnName()).append(" = ").append("#{").append(commonColumnDto.getFieldName()).append("}").append("\n");
        }

        builder.append(BLANK_4).append(BLANK_4).append(BLANK_4).append("</if>");

        return builder.toString();
    }

    /**
     * 데이터 형에 따라 Value 에 대한 구문을 가져온다.
     *
     * @param commonColumnDto 컬럼 정보
     * @return Value 에 대한 구문
     */
    private String getValueWord(CommonColumnDto commonColumnDto) {
        if (commonColumnDto.getJavaDataType() == JavaDataType.LOCAL_DATE_TIME
                || commonColumnDto.getJavaDataType() == JavaDataType.LOCAL_DATE
                || commonColumnDto.getJavaDataType() == JavaDataType.LOCAL_TIME) {
            return "CURRENT_TIMESTAMP";
        } else {
            return "#{" + commonColumnDto.getFieldName() + "}";
        }
    }
}
