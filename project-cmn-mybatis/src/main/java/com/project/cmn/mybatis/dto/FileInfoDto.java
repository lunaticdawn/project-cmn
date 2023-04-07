package com.project.cmn.mybatis.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileInfoDto {
    /**
     * Dto 파일명
     */
    private String dtoFilename;

    /**
     * Dto 파일 전체 경로
     */
    private String dtoPath;

    /**
     * Dto 전체 패키지
     */
    private String dtoPackage;
}
