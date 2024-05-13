package com.project.cmn.gen.mysql.mapper;

import com.project.cmn.gen.mysql.dto.MySQLColumnDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MySQLColumnsMapper {
    /**
     * 테이블의 열 정보를 조회한다.
     *
     * @param tableSchema 열이 포함된 테이블이 속한 스키마(데이터베이스)의 이름. Null 을 허용함
     * @param tableName   열이 포함된 테이블의 이름. Null 을 허용하지 않음
     * @return 테이블의 열 정보
     */
    List<MySQLColumnDto> selectColumnList(@Param("tableSchema") String tableSchema, @Param("tableName") String tableName);
}
