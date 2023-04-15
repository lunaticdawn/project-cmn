package com.project.cmn.mybatis.mariadb.service;

import com.project.cmn.mybatis.dto.FileInfoDto;
import com.project.cmn.mybatis.dto.ProjectInfoDto;
import com.project.cmn.mybatis.mariadb.dto.MariaDbColumnDto;
import com.project.cmn.mybatis.mariadb.mapper.ColumnsMapper;
import com.project.cmn.mybatis.util.JavaDataType;
import com.project.cmn.mybatis.util.MariaDbDataType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MakeFilesForMariaDbService {
    private final ColumnsMapper columnsMapper;

    /**
     * 테이블의 정보를 조회하여 파일을 생성한다.
     *
     * @param projectInfoDto {@link ProjectInfoDto} 파일 생성에 대한 정보
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     */
    public void makeFiles(ProjectInfoDto projectInfoDto, FileInfoDto fileInfoDto) {
        boolean isLocalDate = false;
        boolean isLocalTime = false;
        boolean isLocalDateTime = false;

        List<MariaDbColumnDto> columnsList = columnsMapper.selectColumnList(projectInfoDto.getTableSchema(), projectInfoDto.getTableName());

        for (MariaDbColumnDto mariaDbColumnDto : columnsList) {
            // java.time.LocalDate 타입이 있는지 체크
            if (this.getJavaDataType(mariaDbColumnDto) == JavaDataType.LOCAL_DATE && !isLocalDate) {
                isLocalDate = true;
            }

            // java.time.LocalTime 타입이 있는지 체크
            if (this.getJavaDataType(mariaDbColumnDto) == JavaDataType.LOCAL_TIME && !isLocalTime) {
                isLocalTime = true;
            }

            // java.time.LocalDateTime 타입이 있는지 체크
            if (this.getJavaDataType(mariaDbColumnDto) == JavaDataType.LOCAL_DATE_TIME && !isLocalDateTime) {
                isLocalDateTime = true;
            }
        }

        File dtoFile = new File(fileInfoDto.getDtoPath());

        if (!dtoFile.exists()) {
            dtoFile.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(dtoFile)) {
            IOUtils.write(this.getContent(projectInfoDto, fileInfoDto, columnsList, isLocalDate, isLocalTime, isLocalDateTime), fos, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Dto 파일 내용
     *
     * @param projectInfoDto {@link ProjectInfoDto} 파일 생성에 대한 정보
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     * @param columnsList 테이블의 컬럼 리스트
     * @param isLocalDate Date 타입 존재 여부
     * @param isLocalTime Time 타입 존재 여부
     * @param isLocalDateTime DateTime 타입 존재 여부
     * @return Dto 파일 내용
     */
    private String getContent(ProjectInfoDto projectInfoDto, FileInfoDto fileInfoDto, List<MariaDbColumnDto> columnsList, boolean isLocalDate, boolean isLocalTime, boolean isLocalDateTime) {
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

        if (StringUtils.isNotBlank(projectInfoDto.getTableSchema())) {
            buff.append(" * ").append(projectInfoDto.getTableSchema()).append(".").append(projectInfoDto.getTableName()).append("\n");
        } else {
            buff.append(" * ").append(projectInfoDto.getTableName()).append("\n");
        }

        buff.append(" */").append("\n");
        buff.append("@Getter").append("\n");
        buff.append("@Setter").append("\n");
        buff.append("@ToString").append("\n");
        buff.append("public class ").append(fileInfoDto.getDtoFilename()).append(" {").append("\n");

        for (MariaDbColumnDto mariaDbColumnDto : columnsList) {
            buff.append("    /**").append("\n");
            buff.append("     * ").append(mariaDbColumnDto.getColumnComment()).append("\n");
            buff.append("     */").append("\n");
            buff.append("    @JsonProperty(\"").append(mariaDbColumnDto.getColumnName().toLowerCase()).append("\")").append("\n");
            buff.append("    private ").append(this.getJavaDataType(mariaDbColumnDto).getType()).append(" ").append(CaseUtils.toCamelCase(mariaDbColumnDto.getColumnName(), false, '_')).append(";").append("\n");
            buff.append("\n");
        }

        buff.append("}");

        return buff.toString();
    }

    /**
     * 컬럼의 타입에 해당하는 Java 타입으로 변경한다.
     *
     * @param mariaDbColumnDto {@link MariaDbColumnDto} 컬럼 정보
     * @return 컬럼의 타입에 해당하는 Java 타입
     */
    private JavaDataType getJavaDataType(MariaDbColumnDto mariaDbColumnDto) {
        boolean isUnsigned = mariaDbColumnDto.getColumnType().contains("unsigned");

        JavaDataType javaDataType = MariaDbDataType.getJavaDataType(mariaDbColumnDto.getDataType());

        if (isUnsigned && javaDataType == JavaDataType.INTEGER) {
            javaDataType = JavaDataType.LONG;
        }

        return javaDataType;
    }
}
