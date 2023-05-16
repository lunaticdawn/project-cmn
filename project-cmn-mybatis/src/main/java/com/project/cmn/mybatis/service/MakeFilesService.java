package com.project.cmn.mybatis.service;

import com.project.cmn.mybatis.dto.FileInfoDto;
import com.project.cmn.mybatis.dto.ProjectInfoDto;
import com.project.cmn.mybatis.mariadb.service.MakeFilesForMariaDbService;
import com.project.cmn.mybatis.mssql.service.MakeFilesForMSSqlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Service
public class MakeFilesService {
    private final MakeFilesForMariaDbService makeFilesForMariaDbService;
    private final MakeFilesForMSSqlService makeFilesForMSSqlService;

    /**
     * 파일에 대한 기본정보를 생성하고, DBMS 에 따라 내용을 만들고 생성한다.
     *
     * @param param {@link ProjectInfoDto} 파일 생성에 대한 정보
     */
    public void makeFiles(ProjectInfoDto param) {
        Assert.notNull(param.getDbmsName(), "dbms_name is null!");
        Assert.notNull(param.getProjectPath(), "project_path is null!");
        Assert.notNull(param.getBasePackage(), "base_package is null!");
        Assert.notNull(param.getTableName(), "table_name is null!");
        Assert.notNull(param.getDtoPackage(), "dto_package is null!");

        // 파일명
        String filename = param.getTableName();

        if (StringUtils.isNotBlank(param.getPrefixReplaceByBlank())) {
            filename = RegExUtils.replaceFirst(param.getTableName(), param.getPrefixReplaceByBlank(), "");
        }

        filename = CaseUtils.toCamelCase(filename, true, '_');

        String separator;

        if (File.separator.equals("\\")) {
            separator = "\\\\";
        } else {
            separator = "/";
        }

        // Base Package 까지의 경로
        String basePackageDir = param.getProjectPath()
                + File.separator + "src" + File.separator + "main" + File.separator + "java"
                + File.separator + RegExUtils.replaceAll(param.getBasePackage(), "\\.", separator);

        FileInfoDto fileInfoDto = new FileInfoDto();

        if (StringUtils.isNotBlank(param.getDtoPostfix())) {
            fileInfoDto.setDtoFilename(filename + param.getDtoPostfix());
        } else {
            fileInfoDto.setDtoFilename(filename);
        }

        // Dto 까지의 경로 및 Package
        fileInfoDto.setDtoPath(basePackageDir + File.separator + RegExUtils.replaceAll(param.getDtoPackage(), "\\.", separator) + File.separator + fileInfoDto.getDtoFilename() + ".java");
        fileInfoDto.setDtoPackage(param.getBasePackage() + "." + param.getDtoPackage());

        log.debug("# fileInfoDto: {}", fileInfoDto);

        // DBMS 에 따라 파일을 만든다.
        if (StringUtils.equalsIgnoreCase(param.getDbmsName(), "mariadb")
                || StringUtils.equalsIgnoreCase(param.getDbmsName(), "mysql")) {
            makeFilesForMariaDbService.makeFiles(param, fileInfoDto);
        } else if (StringUtils.equalsIgnoreCase(param.getDbmsName(), "mssql")) {
            makeFilesForMSSqlService.makeFiles(param, fileInfoDto);
        }
    }
}
