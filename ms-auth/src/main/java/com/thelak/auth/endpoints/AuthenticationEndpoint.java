package com.thelak.auth.endpoints;

import com.thelak.auth.services.AuthenticationService;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.auth.models.AuthInfoRequest;
import com.thelak.route.auth.models.AuthLoginRequest;
import com.thelak.route.auth.models.AuthSignupRequest;
import com.thelak.route.auth.models.UserModel;
import com.thelak.route.exceptions.MicroServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationEndpoint implements IAuthenticationService {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    @CrossOrigin
    @RequestMapping(value = AUTH_INFO, method = {RequestMethod.POST})
    public UserModel info(@RequestBody AuthInfoRequest request) throws MicroServiceException {
        return authenticationService.info(request);
    }

    @Override
    @CrossOrigin
    @RequestMapping(value = AUTH_SIGN_UP, method = {RequestMethod.POST})
    public UserModel signUp(@RequestBody AuthSignupRequest request) throws MicroServiceException {
        return authenticationService.signUp(request);
    }

    @Override
    @CrossOrigin
    @RequestMapping(value = AUTH_LOGIN, method = {RequestMethod.POST})
    public UserModel login(@RequestBody AuthLoginRequest request) throws MicroServiceException {
        return authenticationService.login(request);
    }

}
