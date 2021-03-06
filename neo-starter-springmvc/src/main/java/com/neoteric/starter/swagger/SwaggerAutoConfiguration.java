package com.neoteric.starter.swagger;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.neoteric.starter.mvc.StarterMvcProperties;
import io.swagger.models.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;
import springfox.documentation.swagger2.web.Swagger2Controller;

@Configuration
@ConditionalOnClass({Swagger.class, Swagger2Controller.class})
@ConditionalOnProperty(value = "neostarter.swagger.enabled", matchIfMissing = true)
@EnableConfigurationProperties({SwaggerProperties.class, StarterMvcProperties.class})
public class SwaggerAutoConfiguration {

    @Import({Swagger2DocumentationConfiguration.class, BeanValidatorPluginsConfiguration.class})
    @PropertySource("classpath:swagger-defaults.properties")
    static class SwaggerConfiguration {
    }

    @Autowired
    SwaggerProperties swaggerProperties;

    @Autowired
    StarterMvcProperties starterMvcProperties;


    @Bean
    Docket docket() {
        Contact contact = new Contact(swaggerProperties.getContact().getName(),
                swaggerProperties.getContact().getUrl(),
                swaggerProperties.getContact().getEmail());

        ApiInfo apiInfo = new ApiInfo(swaggerProperties.getTitle(),
                swaggerProperties.getDescription(),
                swaggerProperties.getVersion(),
                swaggerProperties.getTermsOfServiceUrl(),
                contact,
                swaggerProperties.getLicense(),
                swaggerProperties.getLicenseUrl());

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(getPackage())
                .paths(getPath())
                .build()
                .apiInfo(apiInfo);
    }

    private Predicate<String> getPath() {
        Predicate<String> paths;
        if (Strings.isNullOrEmpty(starterMvcProperties.getApi().getPath())) {
            paths = PathSelectors.any();
        } else {
            paths = PathSelectors.ant(starterMvcProperties.getApi().getPath() + "/**");
        }
        return paths;
    }

    private Predicate<RequestHandler> getPackage() {
        Predicate<RequestHandler> packages;
        if (Strings.isNullOrEmpty(swaggerProperties.getResourcePackage())) {
            packages = RequestHandlerSelectors.any();
        } else {
            packages = RequestHandlerSelectors.basePackage(swaggerProperties.getResourcePackage());
        }
        return packages;
    }
}
