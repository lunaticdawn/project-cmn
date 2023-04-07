package com.project.cmn.configuration.datasource;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

/**
 * project.datasource 에 설정되어 있는 XADataSource 를 등록한다.
 * ComponentScan 으로 Service 나 Mapper 가 등록되기 전에 등록하기 위해 {@link BeanDefinitionRegistryPostProcessor} 인터페이스를 구현하고
 * 설정들이 주입되기 전에 실행되기 때문에 설정을 가져오기 위한 {@link EnvironmentAware} 인터페이스를 구현한다.
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = "project.datasource", value = "type", havingValue = "xa")
public class RegistryXADataSource implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
    private DataSourceConfig dataSourceConfig;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        dataSourceConfig = DataSourceConfig.init(environment);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        log.info("# RegistryXADataSource");
        // Atomikos 라이브러리가 자동으로 JTA를 구성해주기 때문에 Transaction에 대한 설정은 별도로 하지 않는다.
        this.registerXADataSource(registry);
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.debug("# postProcessBeanFactory");
    }

    /**
     * JTA 사용을 위해 {@link AtomikosDataSourceBean}에 대한 정의를 등록한다.
     *
     * @param registry {@link BeanDefinitionRegistry}
     */
    private void registerXADataSource(BeanDefinitionRegistry registry) {
        AbstractBeanDefinition beanDefinition;

        for (DataSourceItem item : dataSourceConfig.getItemList()) {
            if (!item.isEnabled()) {
                continue;
            }

            MutablePropertyValues propertyValues = new MutablePropertyValues();

            propertyValues.addPropertyValue("uniqueResourceName", item.getDatasourceName());
            propertyValues.addPropertyValue("xaDataSourceClassName", item.getDriverClassName());
            propertyValues.addPropertyValue("xaProperties", item.getPropertiesForXADataSource());

            if (item.getMaximumPoolSize() != 0) {
                propertyValues.addPropertyValue("maxPoolSize", item.getMaximumPoolSize());
            }

            if (item.getConnectionTimeout() != 0) {
                propertyValues.addPropertyValue("borrowConnectionTimeout", item.getConnectionTimeout());
            }

            if (StringUtils.isNotBlank(item.getConnectionTestQuery())) {
                propertyValues.addPropertyValue("testQuery", item.getConnectionTestQuery());
            }

            beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(AtomikosDataSourceBean.class)
                    .getBeanDefinition();

            beanDefinition.setPropertyValues(propertyValues);

            log.info("# AtomikosDataSourceBean({}) Register.", item.getDatasourceName());
            registry.registerBeanDefinition(item.getDatasourceName(), beanDefinition);
        }
    }
}
