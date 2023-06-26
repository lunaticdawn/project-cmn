package com.project.cmn.mybatis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class ProjectInfoDto {
    /**
     * DBMS 이름
     */
    @NotBlank
    @JsonProperty("dbms_name")
    private String dbmsName;

    /**
     * 프로젝트의 절대 경로
     */
    @NotBlank
    @JsonProperty("project_absolute_path")
    private String projectAbsolutePath;

    /**
     * 기본 패키지
     */
    @NotBlank
    @JsonProperty("base_package")
    private String basePackage;

    /**
     * 작업/업무 패키지
     */
    @NotBlank
    @JsonProperty("work_package")
    private String workPackage;

    /**
     * 테이블 카탈로그
     */
    @JsonProperty("table_catalog")
    private String tableCatalog;

    /**
     * 테이블 스키마
     */
    @JsonProperty("table_schema")
    private String tableSchema;

    /**
     * 테이블 이름
     */
    @NotBlank
    @JsonProperty("table_name")
    private String tableName;

    /**
     * 공백으로 변경할 테이블 접두어. ex) TB_
     */
    @JsonProperty("prefix_replace_by_blank")
    private String prefixReplaceByBlank;

    /**
     * Dto 파일의 접미어 ex) Dto
     */
    @JsonProperty("dto_postfix")
    private String dtoPostfix;

    /**
     * Mapper 파일을 저장할 경로.
     * 프로젝트의 절대 경로/src/main/resources 이후의 경로
     */
    @JsonProperty("mapper_path")
    private String mapperPath;

    /**
     * Mapper 파일의 접미어 ex) Mapper
     */
    @JsonProperty("mapper_postfix")
    private String mapperPostfix;
}