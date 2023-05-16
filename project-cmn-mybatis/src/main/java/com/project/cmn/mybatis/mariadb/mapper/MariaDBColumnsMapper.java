package com.project.cmn.mybatis.mariadb.mapper;

import com.project.cmn.mybatis.mariadb.dto.MariaDbColumnDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;

import java.util.List;

@Mapper
public interface MariaDBColumnsMapper {
    /**
     * 테이블의 열 정보를 조회한다.
     *
     * @param tableSchema 열이 포함된 테이블이 속한 스키마(데이터베이스)의 이름. Null 을 허용함
     * @param tableName   열이 포함된 테이블의 이름. Null 을 허용하지 않음
     * @return 테이블의 열 정보
     */
    List<MariaDbColumnDto> selectColumnList(@Param("tableSchema") String tableSchema, @NonNull @Param("tableName") String tableName);
}
