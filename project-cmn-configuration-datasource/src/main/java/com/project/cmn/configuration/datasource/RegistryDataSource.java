package com.project.cmn.configuration.datasource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.lang.NonNull;

/**
 * project.datasource 에 설정되어 있는 DataSource 와 Transaction 을 등록한다.
 * ComponentScan 으로 Service 나 Mapper 가 등록되기 전에 등록하기 위해 {@link BeanDefinitionRegistryPostProcessor} 인터페이스를 구현하고
 * 설정들이 주입되기 전에 실행되기 때문에 설정을 가져오기 위한 {@link EnvironmentAware} 인터페이스를 구현한다.
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = "project.datasource", name = "type", havingValue = "ds")
public class RegistryDataSource implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
    private DataSourceConfig dataSourceConfig;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.dataSourceConfig = DataSourceConfig.init(environment);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        log.info("# RegistryDataSource");

        this.registerDataSource(registry);
        this.registerTransactionManager(registry);
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.debug("# postProcessBeanFactory");
    }

    /**
     * {@link javax.sql.DataSource}에 대한 정의를 등록한다.
     *
     * @param registry {@link BeanDefinitionRegistry}
     */
    private void registerDataSource(BeanDefinitionRegistry registry) {
        AbstractBeanDefinition beanDefinition;

        for (DataSourceItem item : dataSourceConfig.getItemList()) {
            if (!item.isEnabled()) {
                continue;
            }

            if (item.isLazyConnection()) {
                beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(LazyConnectionDataSourceProxy.class)
                        .addConstructorArgValue(new HikariDataSource(item.getHikariConfig()))
                        .getBeanDefinition();
            } else {
                beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(HikariDataSource.class)
                        .addPropertyValue("dataSource", new HikariDataSource(item.getHikariConfig()))
                        .getBeanDefinition();
            }

            log.info("# DataSource({}) Register.", item.getDatasourceName());
            registry.registerBeanDefinition(item.getDatasourceName(), beanDefinition);
        }
    }

    /**
     * {@link DataSourceTransactionManager}에 대한 정의를 등록한다.
     *
     * @param registry {@link BeanDefinitionRegistry}
     */
    private void registerTransactionManager(BeanDefinitionRegistry registry) {
        AbstractBeanDefinition beanDefinition;

        for (DataSourceItem item : dataSourceConfig.getItemList()) {
            if (!item.isEnabled() || StringUtils.isBlank(item.getTransactionName())) {
                continue;
            }

            beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class)
                    .addConstructorArgReference(item.getDatasourceName())
                    .getBeanDefinition();

            if (item.isPrimary()) {
                beanDefinition.setPrimary(true);
            }

            log.info("# Transaction({}) Register.", item.getTransactionName());
            registry.registerBeanDefinition(item.getTransactionName(), beanDefinition);
        }
    }
}
