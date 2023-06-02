package com.project.cmn.configuration.datasource;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * DataSource 설정
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "project.datasource")
public class DataSourceConfig {
    /**
     * {@link Environment}에서 project.datasource 설정을 가져와 {@link DataSourceConfig}로 변환한다.
     *
     * @param environment {@link Environment}
     * @return {@link DataSourceConfig}
     */
    public static DataSourceConfig init(Environment environment) {
        return Binder.get(environment).bindOrCreate("project.datasource", DataSourceConfig.class);
    }

    /**
     * 사용할 DataSource 타입
     * ds: {@link javax.sql.DataSource}, xa: {@link javax.sql.XADataSource}
     */
    private String type;

    /**
     * 여러 개의 {@link com.zaxxer.hikari.HikariDataSource} 또는 {@link javax.sql.XADataSource} 생성에 필요한 설정
     */
    private List<DataSourceItem> itemList;
}
