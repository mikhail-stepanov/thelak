package com.thelak.site.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@EnableWebMvc
@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class RestConfiguration implements WebMvcConfigurer {

    @Value("${spring.resources.static-locations}")
    private String vueHome;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            registry.addResourceHandler("/**")
                    .addResourceLocations(vueHome);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("GET", "POST", "OPTIONS", "PUT", "DELETE");
    }
}