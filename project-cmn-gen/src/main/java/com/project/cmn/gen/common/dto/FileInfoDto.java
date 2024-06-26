package com.project.cmn.gen.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

@Getter
@Setter
@ToString
public class FileInfoDto {
    /**
     * 기본 파일명. 테이블명을 Camel Case 로 변환한 것
     */
    private String basicFilename;

    /**
     * Dto 파일명
     */
    private String dtoFilename;

    /**
     * Dto 파일의 절대 경로. 파일명 포함
     */
    private String dtoAbsolutePath;

    /**
     * 생성된 Dto 파일
     */
    private File dtoFile;

    /**
     * Dto 전체 패키지
     */
    private String dtoPackage;

    /**
     * Mapper 파일명
     */
    private String mapperFilename;

    /**
     * Mapper 파일의 절대 경로. 파일명 포함
     */
    private String mapperAbsolutePath;

    /**
     * 생성된 Mapper 파일
     */
    private File mapperFile;

    /**
     * Mapper 의 namespace
     */
    private String mapperNamespace;
}
