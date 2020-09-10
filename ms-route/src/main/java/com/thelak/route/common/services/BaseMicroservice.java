package com.thelak.route.common.services;


import com.google.gson.Gson;
import com.thelak.route.common.models.ErrorResponse;
import com.thelak.route.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Slf4j
public class BaseMicroservice implements ApplicationContextAware {


    private String serviceName;
    protected final RestTemplate restTemplate;

    private int retry;
    private int sleepTime;
    private String applicationGroup;

    private Map<String, Function<ErrorResponse, MicroServiceException>> _exceptionFactory = new HashMap<>();

    protected BaseMicroservice(String serviceName, RestTemplate restTemplate) {
        this.serviceName = serviceName;
        this.restTemplate = restTemplate;

        initializeExceptions();
    }

    protected String buildUrl(String method) {
        String url = String.format("http://%s%s%s", applicationGroup, serviceName, method);
        return url;
    }

    protected <T> T retry(Callable<T> callable) throws MicroServiceException {
        int attempt = 0;
        while (attempt++ < retry) {
            try {
                try {
                    return callable.call();
                } catch (ResourceAccessException | IllegalStateException ex) {
                    if (ex.getCause() == null) {
                        log.error(ex.getMessage());
                    } else if (ex.getCause() instanceof UnknownHostException) {
                        log.error("Can't determine host");
                    } else if (ex.getCause() instanceof SocketException) {
                        log.error("Lost connection on request");
                    } else {
                        log.error(ex.getCause().getMessage());
                    }
//                    attempt--;
                    Thread.sleep(sleepTime * 1000);
                } catch (HttpStatusCodeException ex) {
                    throw createException(ex);
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    if (log.isDebugEnabled()) {
                        log.error(ex.getMessage(), ex);
                    }
                    Thread.sleep(sleepTime * 1000);
                }
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            }
        }
        throw new MsInternalErrorException("Timeout");
    }

    private MicroServiceException createException(HttpStatusCodeException ex) {
        String body = ex.getResponseBodyAsString();
        ErrorResponse errorResponse = new Gson().fromJson(body, ErrorResponse.class);

        if (errorResponse == null || errorResponse.getClassName() == null || errorResponse.getClassName().isEmpty()) {
            return new MicroServiceException(body);
        }

        if (!_exceptionFactory.containsKey(errorResponse.getClassName())) {
            return new MicroServiceException(body);
        }
        return _exceptionFactory.get(errorResponse.getClassName()).apply(errorResponse);
    }

    private void initializeExceptions() {

        _exceptionFactory.put(MsObjectNotFoundException.class.getSimpleName(), error -> new MsObjectNotFoundException(error.getError(), error.getError()));

        _exceptionFactory.put(MsAlreadyExistsException.class.getSimpleName(), error -> new MsAlreadyExistsException());

        _exceptionFactory.put(MsNotAllowedException.class.getSimpleName(), error -> new MsNotAllowedException());

        _exceptionFactory.put(MsInternalErrorException.class.getSimpleName(), error -> new MsInternalErrorException(error.getError()));

        _exceptionFactory.put(MsBadRequestException.class.getSimpleName(), error -> new MsBadRequestException(error.getError()));

        _exceptionFactory.put(MsNotAuthorizedException.class.getSimpleName(), error -> new MsNotAuthorizedException());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        retry = applicationContext.getEnvironment().getProperty("ms.retry.count", Integer.class, 10);
        sleepTime = applicationContext.getEnvironment().getProperty("ms.retry.time.sec", Integer.class, 1);
        applicationGroup = applicationContext.getEnvironment().getProperty("application.group", "");
    }
}
