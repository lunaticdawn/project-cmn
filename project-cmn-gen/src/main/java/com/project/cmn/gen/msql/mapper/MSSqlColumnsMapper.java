package com.project.cmn.gen.msql.mapper;

import com.project.cmn.gen.msql.dto.MSSqlColumnDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;

import java.util.List;

@Mapper
public interface MSSqlColumnsMapper {
    /**
     * 테이블의 열 정보를 조회한다.
     *
     * @param tableCatalog 테이블 속한 카탈로그
     * @param tableSchema 테이블이 속한 스키마(데이터베이스)의 이름. Null 을 허용함
     * @param tableName   열이 포함된 테이블의 이름. Null 을 허용하지 않음
     * @return 테이블의 열 정보
     */
    List<MSSqlColumnDto> selectColumnList(@Param("tableCatalog") String tableCatalog, @Param("tableSchema") String tableSchema, @NonNull @Param("tableName") String tableName);
}
