package com.project.cmn.mybatis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

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
     * 프로젝트의 전체 경로
     */
    @NotBlank
    @JsonProperty("project_path")
    private String projectPath;

    /**
     * 기본 패키지
     */
    @NotBlank
    @JsonProperty("base_package")
    private String basePackage;

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
     * Dto의 패키지. 기본 패키지 이후의 패키지
     */
    @NotBlank
    @JsonProperty("dto_package")
    private String dtoPackage;

    /**
     * Dto의 접미어 ex) Dto
     */
    @JsonProperty("dto_postfix")
    private String dtoPostfix;
}
