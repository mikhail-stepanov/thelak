package com.thelak.route.auth.services;

import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.auth.models.AuthLoginRequest;
import com.thelak.route.auth.models.AuthSignupRequest;
import com.thelak.route.auth.models.UpdateUserModel;
import com.thelak.route.auth.models.VueHelpModel;
import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import org.springframework.web.client.RestTemplate;

public class AuthenticationService extends BaseMicroservice implements IAuthenticationService {

    public AuthenticationService(RestTemplate restTemplate) {
        super("ms-auth", restTemplate);
    }

    @Override
    public VueHelpModel info() throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(AUTH_INFO), VueHelpModel.class).getBody());
    }

    @Override
    public VueHelpModel signUp(AuthSignupRequest request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_SIGN_UP), request, VueHelpModel.class).getBody());
    }

    @Override
    public String login(AuthLoginRequest request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_LOGIN), request, String.class).getBody());
    }

    @Override
    public String refresh() throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(AUTH_REFRESH), String.class).getBody());
    }

    @Override
    public VueHelpModel updateUser(UpdateUserModel user) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_REFRESH), user, VueHelpModel.class).getBody());
    }
}
