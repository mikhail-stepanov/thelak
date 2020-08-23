package com.thelak.core.configurations;

import com.thelak.core.filters.HttpServletRequestFilter;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.auth.services.AuthenticationService;
import com.thelak.route.video.interfaces.IVideoService;
import com.thelak.route.video.servies.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;


@Configuration
@EnableDiscoveryClient
public class MicroservicesConfiguration {

    private final DiscoveryClient discoveryClient;
    private final ApplicationContext applicationContext;

    @Autowired
    public MicroservicesConfiguration(DiscoveryClient discoveryClient, ApplicationContext applicationContext) {
        this.discoveryClient = discoveryClient;
        this.applicationContext = applicationContext;
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        System.setProperty("http.maxConnections", "50");
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        template.getInterceptors().add((request, body, execution) -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest currentRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
                Collections.list(currentRequest.getHeaderNames()).stream()
                        .filter(header -> header.startsWith("x-thelak-"))
                        .forEach(header -> {
                            request.getHeaders().set(header, currentRequest.getHeader(header));
                        });
            }
            return execution.execute(request, body);
        });
        List<HttpMessageConverter<?>> messageConverters = template.getMessageConverters();
        messageConverters.removeIf(httpMessageConverter -> httpMessageConverter instanceof MappingJackson2XmlHttpMessageConverter);
        template.setMessageConverters(messageConverters);
        return template;
    }


    @Bean
    IAuthenticationService authenticationService() {
        AuthenticationService service = new AuthenticationService(restTemplate());
        service.setApplicationContext(applicationContext);
        return service;
    }

    @Bean
    IVideoService videoService() {
        return new VideoService(restTemplate());
    }


    @Bean
    public FilterRegistrationBean filterRegistrationService() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestFilter());
        return registration;
    }
}
