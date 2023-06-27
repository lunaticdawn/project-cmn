package com.project.cmn.mybatis.mssql.service;

import com.project.cmn.mybatis.dto.CommonColumnDto;
import com.project.cmn.mybatis.dto.FileInfoDto;
import com.project.cmn.mybatis.dto.ProjectInfoDto;
import com.project.cmn.mybatis.mssql.dto.MSSqlColumnDto;
import com.project.cmn.mybatis.mssql.mapper.MSSqlColumnsMapper;
import com.project.cmn.mybatis.service.CommonMakeFiles;
import com.project.cmn.mybatis.util.JavaDataType;
import com.project.cmn.mybatis.util.MSSqlDataType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MakeFilesForMSSqlService extends CommonMakeFiles {
    private final MSSqlColumnsMapper columnsMapper;

    /**
     * 테이블의 정보를 조회하여 파일을 생성한다.
     *
     * @param projectInfoDto {@link ProjectInfoDto} 파일 생성에 대한 정보
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     */
    public void makeFiles(ProjectInfoDto projectInfoDto, FileInfoDto fileInfoDto) {
        File dtoFile = new File(fileInfoDto.getDtoAbsolutePath());

        if (!dtoFile.getParentFile().exists()) {
            dtoFile.getParentFile().mkdirs();
        }

        File mapperFile = new File(fileInfoDto.getMapperAbsolutePath());

        if (!mapperFile.getParentFile().exists()) {
            mapperFile.getParentFile().mkdirs();
        }

        try {
            List<MSSqlColumnDto> columnsList = columnsMapper.selectColumnList(projectInfoDto.getTableCatalog(), projectInfoDto.getTableSchema(), projectInfoDto.getTableName());
            List<CommonColumnDto> commonColumnDtoList = new ArrayList<>();

            for (MSSqlColumnDto msSqlColumnDto : columnsList) {
                CommonColumnDto commonColumnDto = new CommonColumnDto();

                BeanUtils.copyProperties(msSqlColumnDto, commonColumnDto);

                commonColumnDto.setColumnName(msSqlColumnDto.getColumnName().toUpperCase());
                commonColumnDto.setFieldName(CaseUtils.toCamelCase(msSqlColumnDto.getColumnName(), false, '_'));
                commonColumnDto.setJavaDataType(this.getJavaDataType(msSqlColumnDto));

                commonColumnDtoList.add(commonColumnDto);
            }

            String content = this.getDtoContent(fileInfoDto, commonColumnDtoList);

            Files.writeString(dtoFile.toPath(), content, StandardCharsets.UTF_8);

            content = this.getMapperContent(fileInfoDto, commonColumnDtoList);

            Files.writeString(mapperFile.toPath(), content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 컬럼의 타입에 해당하는 Java 타입으로 변경한다.
     *
     * @param columnDto {@link MSSqlColumnDto} 컬럼 정보
     * @return 컬럼의 타입에 해당하는 Java 타입
     */
    private JavaDataType getJavaDataType(MSSqlColumnDto columnDto) {
        return MSSqlDataType.getJavaDataType(columnDto.getDataType());
    }
}