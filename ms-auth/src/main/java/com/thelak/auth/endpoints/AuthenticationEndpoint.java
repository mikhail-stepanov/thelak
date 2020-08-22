package com.thelak.auth.endpoints;

import com.thelak.auth.services.AuthenticationService;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.auth.models.AuthLoginRequest;
import com.thelak.route.auth.models.AuthSignupRequest;
import com.thelak.route.auth.models.UserModel;
import com.thelak.route.exceptions.MicroServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "Authorization API", produces = "application/json")
public class AuthenticationEndpoint implements IAuthenticationService {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    @ApiOperation(value = "Get user info by token")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = AUTH_INFO, method = {RequestMethod.GET})
    public UserModel info() throws MicroServiceException {
        return authenticationService.info();
    }

    @Override
    @ApiOperation(value = "Sign up")
    @RequestMapping(value = AUTH_SIGN_UP, method = {RequestMethod.POST})
    public UserModel signUp(@RequestBody AuthSignupRequest request) throws MicroServiceException {
        return authenticationService.signUp(request);
    }

    @Override
    @ApiOperation(value = "Login user")
    @RequestMapping(value = AUTH_LOGIN, method = {RequestMethod.POST})
    public String login(@RequestBody AuthLoginRequest request) throws MicroServiceException {
        return authenticationService.login(request);
    }

    @Override
    @ApiOperation(value = "Refresh token")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = AUTH_REFRESH, method = {RequestMethod.GET})
    public String refresh() throws MicroServiceException {
        return authenticationService.refresh();
    }

}
