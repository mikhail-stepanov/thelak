package com.thelak.core.interceptors;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MetricInterceptor extends HandlerInterceptorAdapter {

    private final String application;
    private final MeterRegistry meterRegistry;

    private final ConcurrentHashMap<HttpServletRequest, Timer.Sample> startTimes = new ConcurrentHashMap<>();

    public MetricInterceptor(MeterRegistry meterRegistry, String application) {
        this.application = application;
        this.meterRegistry = meterRegistry;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        buildMetricName(handler).ifPresent(name -> {
            startTimes.put(request, Timer.start(meterRegistry));
        });
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        buildMetricName(handler).ifPresent(name -> {
            //пишем время
            startTimes.get(request).stop(meterRegistry.timer(name));
        });
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        buildMetricName(handler).ifPresent(name -> {
            if (HttpStatus.valueOf(response.getStatus()).isError()) {
                startTimes.get(request).stop(meterRegistry.timer(name + ".error"));
            }
        });
        startTimes.remove(request);
    }

    private Optional<String> buildMetricName(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String[] packageParts = handlerMethod.getBeanType().getName().split("\\.");
            String className = packageParts[packageParts.length - 1];
            return Optional.of(String.format("%s.%s.%s", application, className, handlerMethod.getMethod().getName()));
        }
        return Optional.empty();
    }
}
