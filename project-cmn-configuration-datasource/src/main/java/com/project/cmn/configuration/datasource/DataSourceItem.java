package com.project.cmn.configuration.datasource;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 여러 개의 {@link com.zaxxer.hikari.HikariDataSource} 또는 {@link javax.sql.XADataSource} 생성에 필요한 설정
 * #project.datasource.item-list
 */
@Getter
@Setter
@ToString
@ConfigurationProperties
public class DataSourceItem {
    /**
     * DataSource 사용여부
     * #project.datasource.item-list.enabled
     */
    private boolean enabled;

    /**
     * &#064;Primary 선언 여부
     * #project.datasource.item-list.primary
     */
    private boolean primary;

    /**
     * {@link org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy} 사용여부
     * #project.datasource.item-list.lazy-connection
     */
    private boolean lazyConnection;

    /**
     * DataSource 이름. 필수
     * 해당 이름을 pool name 으로 사용
     * #project.datasource.item-list.datasource-name
     */
    private String datasourceName;

    /**
     * Transaction 이름. 옵션. 없으면 생성안함
     * #project.datasource.item-list.transaction-name
     */
    private String transactionName;

    /**
     * JDBC 드라이버 클래스명. 필수
     * #project.datasource.item-list.driver-class-name
     */
    private String driverClassName;

    /**
     * Database 의 JDBC URL. 필수
     * #project.datasource.item-list.url
     */
    private String url;

    /**
     * Database 의 사용자명. 필수
     * #project.datasource.item-list.user
     */
    private String user;

    /**
     * Database 의 비밀번호. 필수
     * #project.datasource.item-list.password
     */
    private String password;

    /**
     * 연결 테스트 쿼리. 드라이버가 JDBC4를 지원하면 설정하지 않음
     * #project.datasource.item-list.connection-test-query
     */
    private String connectionTestQuery;

    /**
     * client가 pool로부터 connection을 얻기위해 기다리는 시간
     * 초단위. 기본 30초
     * #project.datasource.item-list.connection-timeout
     */
    private int connectionTimeout;

    /**
     * connection에 pool에서 idle 상태로 존재하는 시간
     * 초단위. 기본 10분. 최소 10초
     * #project.datasource.item-list.idle-timeout
     */
    private int idleTimeout;

    /**
     * 데이터베이스나 네트워크 인프라에 의해 타임아웃 상태가 되는 것을 방지하기 위해 설정
     * maxLifetime 값보다는 작아야 함.
     * 초단위. 기본 0(사용안함). 최소 30초
     * #project.datasource.item-list.keepalive-time
     */
    private int keepaliveTime;

    /**
     * 커넥션의 최대 유지 시간. 이 시간이 지난 커넥션 중에서 사용중인 커넥션은 종료된 이후에 풀에서 제거한다.
     * 초단위. 기본 30분. 최소 30초
     * #project.datasource.item-list.max-lifetime
     */
    private int maxLifetime;

    /**
     * pool에서 유지하려고 하는 idle connection의 최소 수
     * 기본은 maximumPoolSize에 설정값과 동일
     * #project.datasource.item-list.minimum-idle
     */
    private int minimumIdle;

    /**
     * pool에서 관리하는 connection의 최대 수(idle connection + in-use connection)
     * 기본 10개
     * #project.datasource.item-list.maximum-pool-size
     */
    private int maximumPoolSize;

    /**
     * 해당 시간이상 동안 connection을 pool에 반납하지 않는다면 connection 누수로 판단하고 로그를 출력함
     * 초단위. 기본 0(검출하지 않음). 최소 2초
     * #project.datasource.item-list.leak-detection-threshold
     */
    private int leakDetectionThreshold;

    /**
     * project.datasource.item-list 의 설정을 바탕으로 {@link HikariConfig}를 생성하여 반환한다.
     *
     * @return {@link HikariConfig}
     */
    public HikariConfig getHikariConfig() {
        Map<String, Object> map = new HashMap<>();

        // 필수 항목
        map.put("poolName", datasourceName);
        map.put("driverClassName", driverClassName);
        map.put("jdbcUrl", url);
        map.put("username", user);
        map.put("password", password);

        // 선택 항목
        if (StringUtils.isNotBlank(connectionTestQuery)) {
            map.put("connectionTestQuery", connectionTestQuery);
        }

        if (connectionTimeout != 0) {
            map.put("connectionTimeout", TimeUnit.SECONDS.toMillis(connectionTimeout));
        }

        if (idleTimeout != 0) {
            map.put("idleTimeout", TimeUnit.SECONDS.toMillis(idleTimeout));
        }

        if (keepaliveTime != 0) {
            map.put("keepaliveTime", TimeUnit.SECONDS.toMillis(keepaliveTime));
        }

        if (maxLifetime != 0) {
            map.put("maxLifetime", TimeUnit.SECONDS.toMillis(maxLifetime));
        }

        if (minimumIdle != 0) {
            map.put("minimumIdle", minimumIdle);
        }

        if (maximumPoolSize != 0) {
            map.put("maximumPoolSize", maximumPoolSize);
        }

        if (leakDetectionThreshold != 0) {
            map.put("leakDetectionThreshold", TimeUnit.SECONDS.toMillis(leakDetectionThreshold));
        }

        Properties properties = new Properties();

        properties.putAll(map);

        return new HikariConfig(properties);
    }

    public Properties getPropertiesForXADataSource() {
        Properties properties = new Properties();

        // Oracle 의 경우, url 에 대한 속성이 URL 로 대문자임
        if (StringUtils.containsIgnoreCase(url, "oracle")) {
            properties.put("URL", url);
        } else {
            properties.put("url", url);
        }

        properties.put("user", user);
        properties.put("password", password);

        return properties;
    }
}
