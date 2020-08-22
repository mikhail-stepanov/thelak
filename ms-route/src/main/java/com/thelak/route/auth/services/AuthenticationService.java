package com.thelak.route.auth.services;

import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.auth.models.AuthLoginRequest;
import com.thelak.route.auth.models.AuthSignupRequest;
import com.thelak.route.auth.models.UserModel;
import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import org.springframework.web.client.RestTemplate;

public class AuthenticationService extends BaseMicroservice implements IAuthenticationService {

    public AuthenticationService(RestTemplate restTemplate) {
        super("ms-auth", restTemplate);
    }

    @Override
    public UserModel info() throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_INFO), null, UserModel.class).getBody());
    }

    @Override
    public UserModel signUp(AuthSignupRequest request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_SIGN_UP), request, UserModel.class).getBody());
    }

    @Override
    public String login(AuthLoginRequest request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_LOGIN), request, String.class).getBody());
    }

    @Override
    public String refresh() throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_REFRESH), null, String.class).getBody());
    }
}
