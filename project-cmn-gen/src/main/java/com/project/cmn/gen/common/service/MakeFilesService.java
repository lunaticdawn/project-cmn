package com.project.cmn.gen.common.service;

import com.project.cmn.gen.common.dto.FileInfoDto;
import com.project.cmn.gen.common.dto.ProjectInfoDto;
import com.project.cmn.gen.msql.service.MSSqlService;
import com.project.cmn.gen.mysql.service.MySQLService;
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
    private final MySQLService mySQLService;
    private final MSSqlService msSqlService;

    /**
     * 파일에 대한 기본정보를 생성하고, DBMS 에 따라 내용을 만들고 생성한다.
     *
     * @param param {@link ProjectInfoDto} 파일 생성에 대한 정보
     */
    public void makeFiles(ProjectInfoDto param) {
        Assert.notNull(param.getDbmsName(), "dbms_name is null!");
        Assert.notNull(param.getProjectAbsolutePath(), "project_absolute_path is null!");
        Assert.notNull(param.getBasePackage(), "base_package is null!");
        Assert.notNull(param.getWorkPackage(), "work_package is null!");
        Assert.notNull(param.getTableName(), "table_name is null!");

        FileInfoDto fileInfoDto = new FileInfoDto();

        // 기본 파일명
        String basicFilename = param.getTableName();

        if (StringUtils.isNotBlank(param.getPrefixReplaceByBlank())) {
            basicFilename = RegExUtils.replaceFirst(param.getTableName(), param.getPrefixReplaceByBlank(), "");
        }

        basicFilename = CaseUtils.toCamelCase(basicFilename, true, '_');

        fileInfoDto.setBasicFilename(basicFilename);

        String separator;

        if (File.separator.equals("\\")) {
            separator = "\\\\";
        } else {
            separator = "/";
        }

        // Dto 디렉토리의 절대경로
        String dtoAbsolutePath = param.getProjectAbsolutePath()
                + File.separator + "src" + File.separator + "main" + File.separator + "java"
                + File.separator + RegExUtils.replaceAll(param.getBasePackage(), "\\.", separator)
                + File.separator + RegExUtils.replaceAll(param.getWorkPackage(), "\\.", separator)
                + File.separator + "dto";

        // Dto 파일명
        if (StringUtils.isNotBlank(param.getDtoPostfix())) {
            fileInfoDto.setDtoFilename(basicFilename + param.getDtoPostfix());
        } else {
            fileInfoDto.setDtoFilename(basicFilename);
        }

        fileInfoDto.setDtoAbsolutePath(dtoAbsolutePath + File.separator + fileInfoDto.getDtoFilename() + ".java");
        fileInfoDto.setDtoPackage(param.getBasePackage() + "." + param.getWorkPackage() + ".dto");

        // Mapper 디렉토리의 절대경로
        String mapperAbsolutePath = param.getProjectAbsolutePath()
                + File.separator + "src" + File.separator + "main" + File.separator + "resources"
                + File.separator + param.getMapperPath()
                + File.separator + RegExUtils.replaceAll(param.getWorkPackage(), "\\.", separator);

        // Mapper 파일명
        if (StringUtils.isNotBlank(param.getMapperPostfix())) {
            fileInfoDto.setMapperFilename(basicFilename + param.getMapperPostfix());
        } else {
            fileInfoDto.setMapperFilename(basicFilename);
        }

        fileInfoDto.setMapperAbsolutePath(mapperAbsolutePath + File.separator + fileInfoDto.getMapperFilename() + ".xml");
        fileInfoDto.setMapperNamespace(param.getBasePackage() + "." + param.getWorkPackage() + ".mapper." + fileInfoDto.getMapperFilename());

        log.debug("# fileInfoDto: {}", fileInfoDto);

        File dtoFile = new File(fileInfoDto.getDtoAbsolutePath());

        if (!dtoFile.getParentFile().exists()) {
            dtoFile.getParentFile().mkdirs();
        }

        File mapperFile = new File(fileInfoDto.getMapperAbsolutePath());

        if (!mapperFile.getParentFile().exists()) {
            mapperFile.getParentFile().mkdirs();
        }

        fileInfoDto.setDtoFile(dtoFile);
        fileInfoDto.setMapperFile(mapperFile);

        // DBMS 에 따라 파일을 만든다.
        if (StringUtils.equalsIgnoreCase(param.getDbmsName(), "mysql")
                || StringUtils.equalsIgnoreCase(param.getDbmsName(), "mariadb")) {
            mySQLService.makeFiles(param, fileInfoDto);
        } else if (StringUtils.equalsIgnoreCase(param.getDbmsName(), "mssql")) {
            msSqlService.makeFiles(param, fileInfoDto);
        }
    }
}
