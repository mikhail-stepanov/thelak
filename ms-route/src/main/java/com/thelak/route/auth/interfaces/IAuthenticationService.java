package com.thelak.route.auth.interfaces;

import com.thelak.route.auth.models.*;
import com.thelak.route.exceptions.MicroServiceException;

import java.text.ParseException;

public interface IAuthenticationService {

    String AUTH_INFO = "/v1/auth/info";
    String AUTH_SIGN_UP = "/v1/auth/signup";
    String AUTH_LOGIN = "/v1/auth/login";
    String AUTH_REFRESH = "/v1/auth/refresh";
    String AUTH_USER_UPDATE = "/v1/auth/update";


    VueHelpModel info() throws MicroServiceException;

    VueHelpModel signUp(AuthSignupRequest request) throws MicroServiceException, ParseException;

    String login(AuthLoginRequest request) throws MicroServiceException;

    String refresh() throws MicroServiceException;

    VueHelpModel updateUser(UpdateUserModel user) throws MicroServiceException;
}
