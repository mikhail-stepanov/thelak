package com.thelak.core.configurations;

import com.thelak.core.filters.HttpServletRequestFilter;
import com.thelak.route.article.interfaces.IArticleFunctionsService;
import com.thelak.route.article.interfaces.IArticleService;
import com.thelak.route.article.services.ArticleFunctionsService;
import com.thelak.route.article.services.ArticleService;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.auth.services.AuthenticationService;
import com.thelak.route.category.interfaces.ICategoryContentService;
import com.thelak.route.category.interfaces.ICategoryService;
import com.thelak.route.category.services.CategoryContentService;
import com.thelak.route.category.services.CategoryService;
import com.thelak.route.event.interfaces.IEventService;
import com.thelak.route.event.services.EventService;
import com.thelak.route.speaker.interfaces.ISpeakerContentService;
import com.thelak.route.speaker.interfaces.ISpeakerService;
import com.thelak.route.speaker.services.SpeakerContentService;
import com.thelak.route.speaker.services.SpeakerService;
import com.thelak.route.video.interfaces.IVideoFunctionsService;
import com.thelak.route.video.interfaces.IVideoService;
import com.thelak.route.video.servies.VideoFunctionService;
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
        return new AuthenticationService(restTemplate());
    }

    @Bean
    IVideoService videoService() {
        return new VideoService(restTemplate());
    }

    @Bean
    IVideoFunctionsService videoFunctionsService() {
        return new VideoFunctionService(restTemplate());
    }

    @Bean
    ISpeakerService speakerService() {
        return new SpeakerService(restTemplate());
    }

    @Bean
    ISpeakerContentService speakerContentService() {
        return new SpeakerContentService(restTemplate());
    }

    @Bean
    IArticleService articleService() {
        return new ArticleService(restTemplate());
    }

    @Bean
    IArticleFunctionsService articleFunctionsService() {
        return new ArticleFunctionsService(restTemplate());
    }

    @Bean
    ICategoryService categoryService() {
        return new CategoryService(restTemplate());
    }

    @Bean
    ICategoryContentService categoryContentService() {
        return new CategoryContentService(restTemplate());
    }

    @Bean
    IEventService eventService() {
        return new EventService(restTemplate());
    }

    @Bean
    public FilterRegistrationBean filterRegistrationService() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestFilter());
        return registration;
    }
}
