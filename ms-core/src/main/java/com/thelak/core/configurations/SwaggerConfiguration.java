package com.thelak.core.configurations;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {

    @Bean
    public Docket api() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.thelak"))
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())

                .build()
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, Arrays.asList(
                        new ResponseMessageBuilder().code(200).message("Запрос успешно выполнен").build(),
                        new ResponseMessageBuilder().code(400).message("Ошибка в запросе").build(),
                        new ResponseMessageBuilder().code(500).message("Внутренняя ошибка сервиса").build()
                        )
                )
                .globalResponseMessage(RequestMethod.POST, Arrays.asList(
                        new ResponseMessageBuilder().code(200).message("Запрос успешно выполнен").build(),
                        new ResponseMessageBuilder().code(400).message("Ошибка в запросе").build(),
                        new ResponseMessageBuilder().code(500).message("Внутренняя ошибка сервиса").build()
                        )
                );
        return docket;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
