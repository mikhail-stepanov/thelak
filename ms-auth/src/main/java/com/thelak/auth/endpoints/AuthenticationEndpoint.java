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
import com.thelak.route.auth.models.*;
import com.thelak.route.exceptions.*;
import com.thelak.route.payments.models.subscription.SetSubscriptionModel;
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
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
    @ApiOperation(value = "Get user info by token")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = AUTH_INFO, method = {RequestMethod.GET})
    public VueHelpModel info() throws MicroServiceException {
        try {

            UserInfo userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            DbUser user = SelectById.query(DbUser.class, userInfo.getUserId()).selectFirst(objectContext);

            return VueHelpModel.builder()
                    .data(UserModel.builder()
                            .id((Long) user.getObjectId().getIdSnapshot().get("id"))
                            .name(user.getName())
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .city(user.getCity())
                            .country(user.getCountry())
                            .birthday(user.getBirthday())
                            .isSubscribe(user.isIsSubscribe())
                            .subscriptionDate(user.getSubscriptionDate())
                            .build())
                    .status("success").build();

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Sign up")
    @RequestMapping(value = AUTH_SIGN_UP, method = {RequestMethod.POST})
    public VueHelpModel signUp(@RequestBody AuthSignupRequest request) throws MicroServiceException, ParseException {
        if (!checkEmailExists(request.getEmail())) {

            DbUser user = objectContext.newObject(DbUser.class);

            user.setCreatedDate(LocalDateTime.now());
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setCity(request.getCity());
            user.setCountry(request.getCountry());
            user.setBirthday(LocalDate.parse(request.getBirthday(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            user.setSalt(PasswordHelper.generateSalt());
            user.setPassword(PasswordHelper.hashPassword(request.getPassword(), user.getSalt()));

            objectContext.commitChanges();

            return VueHelpModel.builder()
                    .data(UserModel.builder()
                            .id((Long) user.getObjectId().getIdSnapshot().get("id"))
                            .name(user.getName())
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .city(user.getCity())
                            .country(user.getCountry())
                            .birthday(user.getBirthday())
                            .isSubscribe(user.isIsSubscribe())
                            .subscriptionDate(user.getSubscriptionDate())
                            .build())
                    .status("success").build();

        } else
            throw new MsBadRequestException(request.getEmail());
    }

    @Override
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
                        .isSubscribe(user.isIsSubscribe())
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

    @Override
    @ApiOperation(value = "Update user info")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = AUTH_USER_UPDATE, method = {RequestMethod.POST})
    public VueHelpModel updateUser(@RequestBody UpdateUserModel user) throws MicroServiceException {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            DbUser dbUser = SelectById.query(DbUser.class, userInfo.getUserId()).selectFirst(objectContext);
            dbUser.setName(Optional.ofNullable(user.getName()).orElse(dbUser.getName()));
            dbUser.setBirthday(Optional.ofNullable(user.getBirthday()).orElse(dbUser.getBirthday()));
            dbUser.setCity(Optional.ofNullable(user.getCity()).orElse(dbUser.getCity()));
            dbUser.setEmail(Optional.ofNullable(user.getEmail()).orElse(dbUser.getEmail()));
            dbUser.setCountry(Optional.ofNullable(user.getCountry()).orElse(dbUser.getCountry()));
            dbUser.setPhone(Optional.ofNullable(user.getPhone()).orElse(dbUser.getPhone()));
            dbUser.setModifiedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return VueHelpModel.builder()
                    .status("success")
                    .data(UserModel.builder()
                            .id((Long) dbUser.getObjectId().getIdSnapshot().get("id"))
                            .name(dbUser.getName())
                            .email(dbUser.getEmail())
                            .phone(dbUser.getPhone())
                            .city(dbUser.getCity())
                            .country(dbUser.getCountry())
                            .birthday(dbUser.getBirthday())
                            .isSubscribe(dbUser.isIsSubscribe())
                            .subscriptionDate(dbUser.getSubscriptionDate())
                            .build())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Set user subscription")
    @RequestMapping(value = AUTH_USER_SUBSCRIPTION, method = {RequestMethod.POST})
    public VueHelpModel setSubscription(@RequestBody SetSubscriptionModel setSubscriptionModel) throws MicroServiceException {

        try {
            DbUser dbUser = SelectById.query(DbUser.class, setSubscriptionModel.getUserId()).selectFirst(objectContext);
            dbUser.setSubscriptionDate(setSubscriptionModel.getSubscriptionDate());
            dbUser.setIsSubscribe(true);

            objectContext.commitChanges();

            return VueHelpModel.builder()
                    .status("success")
                    .data(UserModel.builder()
                            .id((Long) dbUser.getObjectId().getIdSnapshot().get("id"))
                            .name(dbUser.getName())
                            .email(dbUser.getEmail())
                            .phone(dbUser.getPhone())
                            .city(dbUser.getCity())
                            .country(dbUser.getCountry())
                            .birthday(dbUser.getBirthday())
                            .isSubscribe(dbUser.isIsSubscribe())
                            .subscriptionDate(dbUser.getSubscriptionDate())
                            .build())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
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
