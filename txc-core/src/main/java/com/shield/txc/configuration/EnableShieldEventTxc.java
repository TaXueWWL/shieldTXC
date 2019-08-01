package com.shield.txc.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 15:31
 * @className EnableShieldEventTxc
 * @desc 自动装配注解支持
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ShieldEventTxcConfiguration.class)
public @interface EnableShieldEventTxc {
}
