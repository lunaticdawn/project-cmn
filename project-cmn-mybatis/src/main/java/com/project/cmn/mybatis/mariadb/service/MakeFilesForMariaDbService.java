package com.project.cmn.mybatis.mariadb.service;

import com.project.cmn.mybatis.dto.CommonColumnDto;
import com.project.cmn.mybatis.dto.FileInfoDto;
import com.project.cmn.mybatis.dto.ProjectInfoDto;
import com.project.cmn.mybatis.mariadb.dto.MariaDbColumnDto;
import com.project.cmn.mybatis.mariadb.mapper.MariaDBColumnsMapper;
import com.project.cmn.mybatis.service.CommonMakeFiles;
import com.project.cmn.mybatis.util.JavaDataType;
import com.project.cmn.mybatis.util.MariaDbDataType;
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
public class MakeFilesForMariaDbService extends CommonMakeFiles {
    private final MariaDBColumnsMapper columnsMapper;

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
            List<MariaDbColumnDto> columnsList = columnsMapper.selectColumnList(projectInfoDto.getTableSchema(), projectInfoDto.getTableName());
            List<CommonColumnDto> commonColumnDtoList = new ArrayList<>();

            for (MariaDbColumnDto mariaDbColumnDto : columnsList) {
                CommonColumnDto commonColumnDto = new CommonColumnDto();

                BeanUtils.copyProperties(mariaDbColumnDto, commonColumnDto);

                commonColumnDto.setColumnName(mariaDbColumnDto.getColumnName().toUpperCase());
                commonColumnDto.setFieldName(CaseUtils.toCamelCase(mariaDbColumnDto.getColumnName(), false, '_'));
                commonColumnDto.setJavaDataType(this.getJavaDataType(mariaDbColumnDto));

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
     * @param columnDto {@link MariaDbColumnDto} 컬럼 정보
     * @return 컬럼의 타입에 해당하는 Java 타입
     */
    private JavaDataType getJavaDataType(MariaDbColumnDto columnDto) {
        boolean isUnsigned = columnDto.getColumnType().contains("unsigned");

        JavaDataType javaDataType = MariaDbDataType.getJavaDataType(columnDto.getDataType());

        if (isUnsigned && javaDataType == JavaDataType.INTEGER) {
            javaDataType = JavaDataType.LONG;
        }

        return javaDataType;
    }
}