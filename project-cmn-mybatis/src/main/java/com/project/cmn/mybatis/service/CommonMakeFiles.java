package com.project.cmn.mybatis.service;

import com.project.cmn.mybatis.dto.CommonColumnDto;
import com.project.cmn.mybatis.dto.FileInfoDto;
import com.project.cmn.mybatis.util.JavaDataType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import java.util.List;

public class CommonMakeFiles {
    private static final String TIME_POSTFIX = "_TIME";

    /**
     * Dto 파일 내용
     *
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param columnsList 테이블의 컬럼 리스트
     * @return Dto 파일 내용
     */
    protected String getContent(FileInfoDto fileInfoDto, List<CommonColumnDto> columnsList) {
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

        // Select 문
        builder.append(this.getSelectStatement(fileInfoDto, columnsList)).append("\n");

        // Insert 문
        builder.append(this.getInsertStatement(fileInfoDto, columnsList)).append("\n");

        // Update 문
        builder.append(this.getUpdateStatement(fileInfoDto, columnsList));

        builder.append("</mapper>");

        return builder.toString();
    }

    /**
     * Select 문의 내용을 만들어 반환한다.
     *
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param columnsList 테이블의 컬럼 리스트
     * @return Select 문의 내용
     */
    private String getSelectStatement(FileInfoDto fileInfoDto, List<CommonColumnDto> columnsList) {
        boolean isFirstRow = true;
        String tableName = columnsList.get(0).getTableName();

        StringBuilder builder = new StringBuilder();

        builder.append("    <select id=\"select").append(fileInfoDto.getBasicFilename()).append("\" resultType=\"").append(fileInfoDto.getDtoPackage() + "." + fileInfoDto.getDtoFilename()).append("\">\n");
        builder.append(this.getFullQueryId(fileInfoDto.getMapperNamespace(), "select", fileInfoDto.getBasicFilename()));
        builder.append("        SELECT\n");

        for (CommonColumnDto commonColumnDto : columnsList) {
            if (isFirstRow) {
                builder.append("            ");

                isFirstRow = false;
            } else {
                builder.append("            , ");
            }

            builder.append(commonColumnDto.getColumnName()).append("\n");
        }

        builder.append("        FROM\n");
        builder.append("            ").append(tableName).append("\n");
        builder.append(this.getWhereClause(columnsList));
        builder.append("    </select>\n");

        return builder.toString();
    }

    /**
     * Insert 문의 내용을 만들어 반환한다.
     *
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param columnsList 테이블의 컬럼 리스트
     * @return Insert 문의 내용
     */
    private String getInsertStatement(FileInfoDto fileInfoDto, List<CommonColumnDto> columnsList) {
        boolean isFirstRow = true;
        String tableName = columnsList.get(0).getTableName();

        StringBuilder builder = new StringBuilder();

        builder.append("    <insert id=\"insert").append(fileInfoDto.getBasicFilename()).append("\">\n");
        builder.append(this.getFullQueryId(fileInfoDto.getMapperNamespace(), "insert", fileInfoDto.getBasicFilename()));
        builder.append("        INSERT INTO ").append(tableName).append(" (\n");

        for (CommonColumnDto commonColumnDto : columnsList) {
            // auto_increment 인 컬럼은 skip
            if (StringUtils.equals(commonColumnDto.getExtra(), "auto_increment")) {
                continue;
            }

            if (StringUtils.equals(commonColumnDto.getIsNullable(), "NO")) {
                if (isFirstRow) {
                    builder.append("            ");
                    isFirstRow = false;
                } else {
                    builder.append("            , ");
                }

                builder.append(commonColumnDto.getColumnName()).append("\n");
            } else {
                builder.append("            ").append(this.getConditionClause(commonColumnDto));
                builder.append("                , ").append(commonColumnDto.getColumnName()).append("\n");
                builder.append("            </if>\n");
            }
        }

        builder.append("        ) VALUES (\n");

        isFirstRow = true;

        for (CommonColumnDto commonColumnDto : columnsList) {
            // auto_increment 인 컬럼은 skip
            if (StringUtils.equals(commonColumnDto.getExtra(), "auto_increment")) {
                continue;
            }

            if (StringUtils.equals(commonColumnDto.getIsNullable(), "NO")) {
                if (isFirstRow) {
                    builder.append("            ");
                    isFirstRow = false;
                } else {
                    builder.append("            , ");
                }

                if (StringUtils.endsWith(commonColumnDto.getColumnName(), TIME_POSTFIX)) {
                    builder.append("CURRENT_TIMESTAMP\n");
                } else {
                    builder.append("#{").append(commonColumnDto.getFieldName()).append("}\n");
                }
            } else {
                builder.append("            ").append(this.getConditionClause(commonColumnDto));
                builder.append("                , #{").append(commonColumnDto.getFieldName()).append("}\n");
                builder.append("            </if>\n");
            }
        }

        builder.append("        )\n");
        builder.append("    </insert>\n");

        return builder.toString();
    }

    /**
     * Update 문의 내용을 만들어 반환한다.
     *
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param columnsList 테이블의 컬럼 리스트
     * @return Update 문의 내용
     */
    private String getUpdateStatement(FileInfoDto fileInfoDto, List<CommonColumnDto> columnsList) {
        String tableName = columnsList.get(0).getTableName();

        StringBuilder builder = new StringBuilder();

        builder.append("    <update id=\"update").append(fileInfoDto.getBasicFilename()).append("\">\n");
        builder.append(this.getFullQueryId(fileInfoDto.getMapperNamespace(), "update", fileInfoDto.getBasicFilename()));
        builder.append("        UPDATE ").append(tableName).append("\n");
        builder.append("        <set>\n");

        for (CommonColumnDto commonColumnDto : columnsList) {
            if (StringUtils.equals(commonColumnDto.getColumnKey(), "PRI")) {
                continue;
            }

            if (StringUtils.equals(commonColumnDto.getIsNullable(), "NO")) {
                builder.append("            ").append(this.getConditionClause(commonColumnDto));
                builder.append("                , ");

                if (StringUtils.endsWith(commonColumnDto.getColumnName(), TIME_POSTFIX)) {
                    builder.append(commonColumnDto.getColumnName()).append(" = CURRENT_TIMESTAMP\n");
                } else {
                    builder.append(commonColumnDto.getColumnName()).append(" = ").append("#{" + commonColumnDto.getFieldName() + "}\n");
                }

                builder.append("            </if>\n");
            } else {
                builder.append("            , ");

                if (StringUtils.endsWith(commonColumnDto.getColumnName(), TIME_POSTFIX)) {
                    builder.append(commonColumnDto.getColumnName()).append(" = CURRENT_TIMESTAMP\n");
                } else {
                    builder.append(commonColumnDto.getColumnName()).append(" = ").append("#{" + commonColumnDto.getFieldName() + "}\n");
                }
            }
        }

        builder.append("        </set>\n");
        builder.append(this.getWhereClause(columnsList));
        builder.append("    </update>\n");

        return builder.toString();
    }

    /**
     * 전체 쿼리 아이디를 만들어 반환한다.
     *
     * @param namespace Mapper 의 네임스페이스
     * @param type select, insert, update
     * @param basicFilename 기본 파일명. Table 명의 Camel Case
     * @return 전체 쿼리 아이디
     */
    private String getFullQueryId(String namespace, String type, String basicFilename) {
        return "        /* " + namespace + "." + type + basicFilename + " */\n";
    }

    /**
     * Where 절을 만들어 반환한다.
     *
     * @param columnsList 테이블의 컬럼 리스트
     * @return Where 절
     */
    private String getWhereClause(List<CommonColumnDto> columnsList) {
        StringBuilder builder = new StringBuilder();

        builder.append("        WHERE\n");

        for (CommonColumnDto commonColumnDto : columnsList) {
            if (StringUtils.equals(commonColumnDto.getColumnKey(), "PRI")) {
                builder.append("            ").append(commonColumnDto.getColumnName()).append(" = ").append("#{" + commonColumnDto.getFieldName() + "}\n");
            } else if (StringUtils.equals(commonColumnDto.getIsNullable(), "NO") && commonColumnDto.getJavaDataType() == JavaDataType.STRING) {
                builder.append("            <if test=\"@org.apache.commons.lang3.StringUtils@isNotBlank("+ commonColumnDto.getFieldName() +")\">\n");
                builder.append("                AND ").append(commonColumnDto.getColumnName()).append(" = ").append("#{" + commonColumnDto.getFieldName() + "}\n");
                builder.append("            </if>\n");
            }
        }

        return builder.toString();
    }

    /**
     * 조건문 구문을 만들어 반환한다.
     *
     * @param commonColumnDto 컬럼 정보
     * @return 조건문 구문
     */
    private String getConditionClause(CommonColumnDto commonColumnDto) {
        StringBuilder builder = new StringBuilder();

        if (commonColumnDto.getJavaDataType() == JavaDataType.STRING) {
            builder.append("<if test=\"@org.apache.commons.lang3.StringUtils@isNotBlank("+ commonColumnDto.getFieldName() +")\">\n");
        } else {
            builder.append("<if test=\"").append(commonColumnDto.getColumnName()).append(" != null").append("\">\n");
        }

        return builder.toString();
    }
}