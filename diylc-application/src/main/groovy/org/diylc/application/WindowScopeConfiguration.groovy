package org.diylc.application

import groovy.transform.CompileStatic

import org.springframework.beans.factory.config.CustomScopeConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@CompileStatic
@Configuration
@EnableAspectJAutoProxy
class WindowScopeConfiguration {

    @Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
        Map<String, Object> scopes = new HashMap<String, Object>(1)
        scopes.put("window", new WindowScope())

        CustomScopeConfigurer configurer = new CustomScopeConfigurer()
        configurer.setScopes(scopes)
        return configurer
    }

}
