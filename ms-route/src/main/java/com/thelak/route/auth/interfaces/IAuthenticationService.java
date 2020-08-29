package com.thelak.route.auth.interfaces;

import com.thelak.route.auth.models.AuthLoginRequest;
import com.thelak.route.auth.models.AuthSignupRequest;
import com.thelak.route.auth.models.UserModel;
import com.thelak.route.auth.models.VueHelpModel;
import com.thelak.route.exceptions.MicroServiceException;

public interface IAuthenticationService {

    String AUTH_INFO = "/v1/auth/info";
    String AUTH_SIGN_UP = "/v1/auth/signup";
    String AUTH_LOGIN = "/v1/auth/login";
    String AUTH_REFRESH = "/v1/auth/refresh";

    VueHelpModel info() throws MicroServiceException;

    VueHelpModel signUp(AuthSignupRequest request) throws MicroServiceException;

    String login(AuthLoginRequest request) throws MicroServiceException;

    String refresh() throws MicroServiceException;

}
