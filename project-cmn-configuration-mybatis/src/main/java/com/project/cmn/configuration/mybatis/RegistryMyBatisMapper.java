package com.project.cmn.configuration.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * project.mybatis 에 설정되어 있는 정보로 {@link SqlSessionTemplate}과 {@link MapperScannerConfigurer}을 등록한다.
 * ComponentScan 으로 Service 나 Mapper 가 등록되기 전에 등록하기 위해 {@link BeanDefinitionRegistryPostProcessor} 인터페이스를 구현하고
 * 설정들이 주입되기 전에 실행되기 때문에 설정을 가져오기 위한 {@link EnvironmentAware} 인터페이스를 구현한다.
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = "project.mybatis", value = "enabled", havingValue = "true")
public class RegistryMyBatisMapper implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
    private MyBatisConfig myBatisConfig;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.myBatisConfig = MyBatisConfig.init(environment);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        log.info("# RegistryMyBatisMapper");

        try {
            registerSqlSessionTemplate(registry);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);

            throw new BeanCreationException(e.getMessage(), e);
        }
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.debug("# postProcessBeanFactory");
    }

    /**
     * {@link org.apache.ibatis.session.SqlSessionFactory}, {@link SqlSessionTemplate}과 {@link MapperScannerConfigurer}를 등록한다.
     * DataSource가 연결이 안되어 있다면 오류가 발생한다.
     *
     * @param registry {@link BeanDefinitionRegistry}
     */
    private void registerSqlSessionTemplate(BeanDefinitionRegistry registry) throws ClassNotFoundException {
        AbstractBeanDefinition sqlSessionFactory;
        AbstractBeanDefinition sqlSessionTemplate;
        AbstractBeanDefinition mapperScannerConfigurer;

        for (MyBatisItem item : myBatisConfig.getItemList()) {
            // SqlSessionFactoryBean 정의
            sqlSessionFactory = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionFactoryBean.class)
                    .addPropertyReference("dataSource", item.getDatasourceName())
                    .addPropertyValue("configLocation", new DefaultResourceLoader().getResource(item.getConfigLocation()))
                    .addPropertyValue("mapperLocations", this.getMapperLocation(item))
                    .addPropertyValue("typeAliasesPackage", String.join(",", item.getTypeAliasesPackages()))
                    .getBeanDefinition();

            log.info("# SqlSessionFactory({}) Register.", item.getSqlSessionFactoryName());
            registry.registerBeanDefinition(item.getSqlSessionFactoryName(), sqlSessionFactory);

            // SqlSessionTemplate 정의
            sqlSessionTemplate = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionTemplate.class)
                    .addConstructorArgValue(sqlSessionFactory)
                    .setPrimary(item.isPrimary())
                    .getBeanDefinition();

            log.info("# SqlSessionTemplate({}) Register.", item.getSqlSessionTemplateName());
            registry.registerBeanDefinition(item.getSqlSessionTemplateName(), sqlSessionTemplate);

            // MapperScannerConfigurer 정의
            mapperScannerConfigurer = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class)
                    .addPropertyValue("sqlSessionTemplateBeanName", item.getSqlSessionTemplateName())
                    .addPropertyValue("basePackage", item.getMapperBasePackage())
                    .addPropertyValue("annotationClass", Class.forName(item.getAnnotationClassName()))
                    .getBeanDefinition();

            log.info("# MapperScannerConfigurer({}) Register", item.getSqlSessionTemplateName() + "-mapper");
            registry.registerBeanDefinition(item.getSqlSessionTemplateName() + "-mapper", mapperScannerConfigurer);
        }
    }

    /**
     * Mapper 위치에 대한 설정 정보를 {@link SqlSessionFactoryBean}에 등록할 수 있는 형태로 변경한다.
     *
     * @param item {@link MyBatisItem}
     * @return Mapper 위치에 대한 설정 정보를 {@link SqlSessionFactoryBean}에 등록할 수 있는 형태로 변경한 {@link Resource} 배열
     */
    private Resource[] getMapperLocation(MyBatisItem item) {
        Resource[] mapperResources = null;
        Resource[] tempMapperResources;

        try {
            for (String mapperLocation : item.getMapperLocations()) {
                tempMapperResources = new PathMatchingResourcePatternResolver().getResources(mapperLocation);

                if (mapperResources == null) {
                    mapperResources = tempMapperResources;
                } else {
                    mapperResources = ArrayUtils.addAll(mapperResources, tempMapperResources);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return mapperResources;
    }
}
