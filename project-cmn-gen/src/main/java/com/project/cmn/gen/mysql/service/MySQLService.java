package com.project.cmn.gen.mysql.service;

import com.project.cmn.gen.common.dto.CommonColumnDto;
import com.project.cmn.gen.common.dto.FileInfoDto;
import com.project.cmn.gen.common.dto.ProjectInfoDto;
import com.project.cmn.gen.common.service.MakeContentService;
import com.project.cmn.gen.common.util.JavaDataType;
import com.project.cmn.gen.common.util.MySQLDataType;
import com.project.cmn.gen.mysql.dto.MySQLColumnDto;
import com.project.cmn.gen.mysql.mapper.MySQLColumnsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MySQLService extends MakeContentService {
    private final MySQLColumnsMapper mySQLColumnsMapper;

    /**
     * 테이블의 정보를 조회하여 파일을 생성한다.
     *
     * @param projectInfoDto {@link ProjectInfoDto} 파일 생성에 대한 정보
     * @param fileInfoDto {@link FileInfoDto} 생성할 파일 정보
     */
    public void makeFiles(ProjectInfoDto projectInfoDto, FileInfoDto fileInfoDto) {
        try {
            List<MySQLColumnDto> columnsList = mySQLColumnsMapper.selectColumnList(projectInfoDto.getTableSchema(), projectInfoDto.getTableName());
            List<CommonColumnDto> commonColumnDtoList = new ArrayList<>();

            CommonColumnDto commonColumnDto;

            for (MySQLColumnDto mySQLColumnDto : columnsList) {
                commonColumnDto = new CommonColumnDto();

                BeanUtils.copyProperties(mySQLColumnDto, commonColumnDto);

                commonColumnDto.setColumnName(mySQLColumnDto.getColumnName().toUpperCase());
                commonColumnDto.setFieldName(CaseUtils.toCamelCase(mySQLColumnDto.getColumnName(), false, '_'));
                commonColumnDto.setJavaDataType(this.getJavaDataType(mySQLColumnDto));

                commonColumnDtoList.add(commonColumnDto);
            }

            String content = this.getDtoContent(fileInfoDto, commonColumnDtoList);

            Files.writeString(fileInfoDto.getDtoFile().toPath(), content, StandardCharsets.UTF_8);

            content = this.getMapperContent(fileInfoDto, commonColumnDtoList);

            Files.writeString(fileInfoDto.getMapperFile().toPath(), content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 컬럼의 타입에 해당하는 Java 타입으로 변경한다.
     *
     * @param columnDto {@link MySQLColumnDto} 컬럼 정보
     * @return 컬럼의 타입에 해당하는 Java 타입
     */
    private JavaDataType getJavaDataType(MySQLColumnDto columnDto) {
        boolean isUnsigned = columnDto.getColumnType().contains("unsigned");

        JavaDataType javaDataType = MySQLDataType.getJavaDataType(columnDto.getDataType());

        if (isUnsigned && javaDataType == JavaDataType.INTEGER) {
            javaDataType = JavaDataType.LONG;
        }

        return javaDataType;
    }
}
