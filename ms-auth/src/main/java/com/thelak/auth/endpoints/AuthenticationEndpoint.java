package com.thelak.auth.endpoints;

import com.thelak.auth.util.PasswordHelper;
import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.core.interfaces.ITokenService;
import com.thelak.core.models.UserInfo;
import com.thelak.core.util.RLUCache;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbUser;
import com.thelak.database.entity.DbUserSession;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.auth.models.AuthLoginRequest;
import com.thelak.route.auth.models.AuthSignupRequest;
import com.thelak.route.auth.models.UserModel;
import com.thelak.route.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@RestController
@Api(value = "Authorization API", produces = "application/json")
public class AuthenticationEndpoint extends AbstractMicroservice implements IAuthenticationService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ITokenService tokenService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(AuthenticationEndpoint.class);

    private RLUCache<String, String> tokensCache;

    @PostConstruct
    private void initialize() {
        int ttl = 60_000;
        int max = 10_000;
        tokensCache = new RLUCache<>(ttl, max);

        objectContext = databaseService.getContext();
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get user info by token")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = AUTH_INFO, method = {RequestMethod.GET})
    public UserModel info() throws MicroServiceException {
        try {

            UserInfo userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            DbUser user = SelectById.query(DbUser.class, userInfo.getUserId()).selectFirst(objectContext);

            return UserModel.builder()
                    .id((Long) user.getObjectId().getIdSnapshot().get("id"))
                    .name(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .city(user.getCity())
                    .country(user.getCountry())
                    .birthday(user.getBirthday())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while finding user info");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Sign up")
    @RequestMapping(value = AUTH_SIGN_UP, method = {RequestMethod.POST})
    public UserModel signUp(@RequestBody AuthSignupRequest request) throws MicroServiceException {
        if (!checkEmailExists(request.getEmail())) {

            DbUser user = objectContext.newObject(DbUser.class);

            user.setCreatedDate(LocalDateTime.now());
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setCity(request.getCity());
            user.setCountry(request.getCountry());
            user.setBirthday(request.getBirthday());
            user.setSalt(PasswordHelper.generateSalt());
            user.setPassword(PasswordHelper.hashPassword(request.getPassword(), user.getSalt()));

            objectContext.commitChanges();

            return UserModel.builder()
                    .id((Long) user.getObjectId().getIdSnapshot().get("id"))
                    .name(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .city(user.getCity())
                    .country(user.getCountry())
                    .birthday(user.getBirthday())
                    .build();

        } else
            throw new MsBadRequestException(request.getEmail());
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Login user")
    @RequestMapping(value = AUTH_LOGIN, method = {RequestMethod.POST})
    public String login(@RequestBody AuthLoginRequest request) throws MicroServiceException {
        if (checkEmailExists(request.getEmail())) {

            DbUser user = ObjectSelect.query(DbUser.class)
                    .where(DbUser.EMAIL.eq(request.getEmail()))
                    .selectFirst(objectContext);

            if (user.getPassword().equals(PasswordHelper.hashPassword(request.getPassword(), user.getSalt()))) {

                UserInfo userInfo = UserInfo.builder()
                        .userId((Long) user.getObjectId().getIdSnapshot().get("id"))
                        .userEmail(user.getEmail())
                        .isAdmin(false)
                        .build();

                String token = tokenService.generateToken(userInfo);
                DbUserSession session = objectContext.newObject(DbUserSession.class);
                session.setCreatedDate(LocalDateTime.now());
                session.setSessionToUser(user);
                session.setToken(token);

                objectContext.commitChanges();

                return token;

            } else
                throw new MsNotAuthorizedException();
        } else
            throw new MsObjectNotFoundException("Customer with email: ", request.getEmail());
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Refresh token")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = AUTH_REFRESH, method = {RequestMethod.GET})
    public String refresh() throws MicroServiceException {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            return tokenService.generateToken(userInfo);

        } catch (ExpiredJwtException e) {
            throw new MsNotAuthorizedException();
        }
    }


    private boolean checkEmailExists(String email) {
        try {
            DbUser user = ObjectSelect.query(DbUser.class)
                    .where(DbUser.EMAIL.eq(email))
                    .selectFirst(objectContext);

            return user != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return true;
        }
    }

}
