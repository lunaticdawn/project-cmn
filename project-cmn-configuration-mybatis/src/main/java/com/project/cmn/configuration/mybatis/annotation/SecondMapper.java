package com.project.cmn.configuration.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface for MyBatis mappers.
 *
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * &#064;SecondMapper
 * public interface UserMapper {
 *   // ...
 * }
 * </pre>
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface SecondMapper {
}