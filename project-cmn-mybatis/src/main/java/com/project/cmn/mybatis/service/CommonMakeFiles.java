package com.project.cmn.mybatis.service;

import com.project.cmn.mybatis.dto.CommonColumnDto;
import com.project.cmn.mybatis.dto.FileInfoDto;
import com.project.cmn.mybatis.util.JavaDataType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import java.util.List;

public class CommonMakeFiles {

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

    protected String getMapperContent(FileInfoDto fileInfoDto, List<CommonColumnDto> columnsList) {
        StringBuilder builder = new StringBuilder();

        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
        builder.append("<mapper namespace=\"").append(fileInfoDto.getMapperNamespace()).append("\">\n");

        // Select 문
        builder.append("    <select id=\"select").append(fileInfoDto.getBasicFilename()).append("\" resultType=\"").append(fileInfoDto.getDtoPackage() + "." + fileInfoDto.getDtoFilename()).append("\">\n");
        builder.append("        /* ").append(fileInfoDto.getMapperNamespace() + ".select" + fileInfoDto.getBasicFilename()).append(" */\n");
        builder.append("        SELECT\n");

        boolean isFirstRow = true;

        for (CommonColumnDto commonColumnDto : columnsList) {
            if (isFirstRow) {
                builder.append("            ");

                isFirstRow = false;
            } else {
                builder.append("            , ");
            }

            builder.append(commonColumnDto.getColumnName()).append("\n");
        }

        String tableName = columnsList.get(0).getTableName();

        builder.append("        FROM\n");
        builder.append("            ").append(tableName).append("\n");
        builder.append("        <where>\n");

        for (CommonColumnDto commonColumnDto : columnsList) {
            if (StringUtils.equals(commonColumnDto.getColumnKey(), "PRI")) {
                builder.append("            AND ").append(commonColumnDto.getColumnName()).append(" = ").append("#{" + commonColumnDto.getFieldName() + "}\n");
            } else if (StringUtils.equals(commonColumnDto.getIsNullable(), "NO") && commonColumnDto.getJavaDataType() == JavaDataType.STRING) {
                builder.append("            <if test=\"@org.apache.commons.lang3.StringUtils@isNotBlank("+ commonColumnDto.getFieldName() +")\">\n");
                builder.append("                AND ").append(commonColumnDto.getColumnName()).append(" = ").append("#{" + commonColumnDto.getFieldName() + "}\n");
                builder.append("            </if>\n");
            }
        }

        builder.append("        </where>\n");
        builder.append("    </select>\n");

        // Insert 문
        builder.append("\n    <insert id=\"insert").append(fileInfoDto.getBasicFilename()).append("\">\n");
        builder.append("        /* ").append(fileInfoDto.getMapperNamespace() + ".insert" + fileInfoDto.getBasicFilename()).append(" */\n");
        builder.append("        INSERT INTO ").append(tableName).append(" (\n");

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

                builder.append(commonColumnDto.getColumnName()).append("\n");
            } else {
                if (commonColumnDto.getJavaDataType() == JavaDataType.STRING) {
                    builder.append("            <if test=\"@org.apache.commons.lang3.StringUtils@isNotBlank(" + commonColumnDto.getFieldName() + ")\">\n");
                }

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

                if (StringUtils.endsWith(commonColumnDto.getColumnName(), "_TIME")) {
                    builder.append("CURRENT_TIMESTAMP\n");
                } else {
                    builder.append("#{").append(commonColumnDto.getFieldName()).append("}\n");
                }
            } else {
                if (commonColumnDto.getJavaDataType() == JavaDataType.STRING) {
                    builder.append("            <if test=\"@org.apache.commons.lang3.StringUtils@isNotBlank(" + commonColumnDto.getFieldName() + ")\">\n");
                } else {
                    builder.append("            <if test=\"").append(commonColumnDto.getFieldName()).append(" != null").append("\">\n");
                }

                builder.append("                , #{").append(commonColumnDto.getFieldName()).append("}\n");
                builder.append("            </if>\n");
            }
        }

        builder.append("        )\n");
        builder.append("    </insert>\n");

        // Update 문
        builder.append("\n    <update id=\"update").append(fileInfoDto.getBasicFilename()).append("\">\n");
        builder.append("        /* ").append(fileInfoDto.getMapperNamespace() + ".update" + fileInfoDto.getBasicFilename()).append(" */\n");
        builder.append("        UPDATE ").append(tableName).append("\n");
        builder.append("        <set>\n");

        for (CommonColumnDto commonColumnDto : columnsList) {
            if (StringUtils.equals(commonColumnDto.getColumnKey(), "PRI")) {
                continue;
            }

            if (StringUtils.equals(commonColumnDto.getIsNullable(), "NO")) {
                if (commonColumnDto.getJavaDataType() == JavaDataType.STRING) {
                    builder.append("            <if test=\"@org.apache.commons.lang3.StringUtils@isNotBlank("+ commonColumnDto.getFieldName() +")\">\n");
                } else {
                    builder.append("            <if test=\"").append(commonColumnDto.getColumnName()).append(" != null").append("\">\n");
                }

                if (StringUtils.endsWith(commonColumnDto.getColumnName(), "_TIME")) {
                    builder.append("                , ").append(commonColumnDto.getColumnName()).append(" = CURRENT_TIMESTAMP\n");
                } else {
                    builder.append("                , ").append(commonColumnDto.getColumnName()).append(" = ").append("#{" + commonColumnDto.getFieldName() + "}\n");
                }
            } else {
                if (StringUtils.endsWith(commonColumnDto.getColumnName(), "_TIME")) {
                    builder.append("            , ").append(commonColumnDto.getColumnName()).append(" = CURRENT_TIMESTAMP\n");
                } else {
                    builder.append("            , ").append(commonColumnDto.getColumnName()).append(" = ").append("#{" + commonColumnDto.getFieldName() + "}\n");
                }
            }
        }

        builder.append("        </set>\n");
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

        builder.append("    </update>\n");
        builder.append("</mapper>");

        return builder.toString();
    }
}