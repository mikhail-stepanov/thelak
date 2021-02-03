package com.thelak.auth.endpoints;

import com.thelak.auth.util.PasswordHelper;
import com.thelak.core.endpoints.MicroserviceAdvice;
import com.thelak.core.interfaces.ITokenService;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbNotification;
import com.thelak.database.entity.DbPasswordRestore;
import com.thelak.database.entity.DbUser;
import com.thelak.database.entity.DbUserSession;
import com.thelak.route.article.interfaces.IArticleFunctionsService;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.auth.models.*;
import com.thelak.route.exceptions.*;
import com.thelak.route.payments.models.subscription.SetSubscriptionModel;
import com.thelak.route.smtp.interfaces.IEmailService;
import com.thelak.route.video.interfaces.IVideoService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Api(value = "Authorization API", produces = "application/json")
public class AuthenticationEndpoint extends MicroserviceAdvice implements IAuthenticationService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private IArticleFunctionsService articleFunctionsService;

    @Autowired
    private IVideoService videoService;

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
            ObjectContext objectContext = databaseService.getContext();
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
                            .roles(user.isIsAdmin() ? "admin" : "user")
                            .renew(user.isRenew())
                            .subType(user.getSubType())
                            .build())
                    .status("success").build();

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Sign up")
    @RequestMapping(value = AUTH_SIGN_UP, method = {RequestMethod.POST})
    public VueHelpModel signUp(@RequestBody AuthSignupRequest request) throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();
        if (!checkEmailExists(request.getEmail()) && request.getPassword() != null) {

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
            user.setIsAdmin(false);

            DbNotification notification = objectContext.newObject(DbNotification.class);
            notification.setNotificationToUser(user);
            notification.setContent(true);
            notification.setNews(true);
            notification.setRecommendation(true);
            notification.setSales(true);

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
                            .roles(user.isIsAdmin() ? "admin" : "user")
                            .build())
                    .status("success").build();

        } else
            throw new MsAlreadyExistsException();
    }

    @Override
    @ApiOperation(value = "Login user")
    @RequestMapping(value = AUTH_LOGIN, method = {RequestMethod.POST})
    public ResponseEntity login(@RequestBody AuthLoginRequest request) throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();
        if (checkEmailExists(request.getEmail())) {

            DbUser user = ObjectSelect.query(DbUser.class)
                    .where(DbUser.EMAIL.lower().eq(request.getEmail().toLowerCase()))
                    .selectFirst(objectContext);

            if (user.getPassword().equals(PasswordHelper.hashPassword(request.getPassword(), user.getSalt()))) {

                UserInfo userInfo = UserInfo.builder()
                        .userId((Long) user.getObjectId().getIdSnapshot().get("id"))
                        .userEmail(user.getEmail())
                        .isSubscribe(user.isIsSubscribe())
                        .isAdmin(user.isIsAdmin())
                        .build();

                String token = tokenService.generateToken(userInfo);
                DbUserSession session = objectContext.newObject(DbUserSession.class);
                session.setCreatedDate(LocalDateTime.now());
                session.setSessionToUser(user);
                session.setToken(token);

                objectContext.commitChanges();

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Authorization", token);
                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body(null);
            } else
                objectContext.rollbackChanges();
            throw new MsNotAuthorizedException();
        } else
            objectContext.rollbackChanges();
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
    public ResponseEntity refresh() throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            DbUser dbUser = SelectById.query(DbUser.class, userInfo.getUserId()).selectFirst(objectContext);
            userInfo.setSubscribe(dbUser.isIsSubscribe());
            userInfo.setUserEmail(dbUser.getEmail());
            userInfo.setAdmin(dbUser.isIsAdmin());

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Authorization", tokenService.generateToken(userInfo));
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(null);
        } catch (ExpiredJwtException e) {
            objectContext.rollbackChanges();
            throw new MsNotAuthorizedException();
        }
    }

    @Override
    @RequestMapping(value = AUTH_USER_RESTORE_REQUEST, method = {RequestMethod.GET})
    public Boolean restorePasswordRequest(@RequestParam String email) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();
            DbUser dbUser = ObjectSelect.query(DbUser.class)
                    .where(DbUser.EMAIL.eq(email))
                    .selectFirst(objectContext);

            if (dbUser != null) {
                DbPasswordRestore dbPasswordRestore = objectContext.newObject(DbPasswordRestore.class);
                dbPasswordRestore.setEmail(email);
                dbPasswordRestore.setPasswordRestoreToUser(dbUser);
                dbPasswordRestore.setCreatedDate(LocalDateTime.now());
                dbPasswordRestore.setStatus(false);
                dbPasswordRestore.setUuid(UUID.randomUUID().toString());

                objectContext.commitChanges();

                emailService.sendRestorePassword(email, dbPasswordRestore.getUuid());

                return true;
            }
            return false;
        } catch (ExpiredJwtException e) {
            throw new MsNotAuthorizedException();
        }
    }

    @Override
    @RequestMapping(value = AUTH_USER_RESTORE_CONFIRM, method = {RequestMethod.POST})
    public Boolean restorePasswordConfirm(@RequestBody RestorePasswordRequest request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();
            DbPasswordRestore dbPasswordRestore = ObjectSelect.query(DbPasswordRestore.class)
                    .where(DbPasswordRestore.UUID.eq(request.getUuid()))
                    .orderBy(DbPasswordRestore.CREATED_DATE.desc())
                    .selectFirst(objectContext);

            if (!dbPasswordRestore.isStatus()) {
                dbPasswordRestore.setStatus(true);

                DbUser dbUser = dbPasswordRestore.getPasswordRestoreToUser();

                dbUser.setSalt(PasswordHelper.generateSalt());
                dbUser.setPassword(PasswordHelper.hashPassword(request.getPassword(), dbUser.getSalt()));

                objectContext.commitChanges();

                return true;
            }
            return false;
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
            ObjectContext objectContext = databaseService.getContext();
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
            ObjectContext objectContext = databaseService.getContext();
            DbUser dbUser = SelectById.query(DbUser.class, setSubscriptionModel.getUserId()).selectFirst(objectContext);
            dbUser.setSubscriptionDate(setSubscriptionModel.getSubscriptionDate());
            dbUser.setIsSubscribe(true);
            dbUser.setSubType(setSubscriptionModel.getSubType());
            if (setSubscriptionModel.getSubType().equals("SUBSCRIPTION"))
                dbUser.setRenew(true);

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
    @ApiOperation(value = "Get user notification info")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = AUTH_USER_NOTIFICATION_INFO, method = {RequestMethod.GET})
    public NotificationModel getNotificationInfo() throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();
            UserInfo userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            DbUser dbUser = SelectById.query(DbUser.class, userInfo.getUserId()).selectFirst(objectContext);

            DbNotification notification = dbUser.getUserToNotification().get(0);

            return NotificationModel.builder()
                    .content(notification.isContent())
                    .news(notification.isNews())
                    .recommendation(notification.isContent())
                    .sales(notification.isSales())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Update user notification info")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = AUTH_USER_NOTIFICATION_UPDATE, method = {RequestMethod.POST})
    public NotificationModel updateNotificationInfo(@RequestBody NotificationModel notificationModel) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();
            UserInfo userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            DbUser dbUser = SelectById.query(DbUser.class, userInfo.getUserId()).selectFirst(objectContext);

            DbNotification notification = dbUser.getUserToNotification().get(0);
            notification.setSales(notificationModel.getSales());
            notification.setRecommendation(notificationModel.getRecommendation());
            notification.setNews(notificationModel.getNews());
            notification.setContent(notificationModel.getContent());

            objectContext.commitChanges();

            return NotificationModel.builder()
                    .content(notification.isContent())
                    .news(notification.isNews())
                    .recommendation(notification.isContent())
                    .sales(notification.isSales())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of users")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = AUTH_USER_INFO, method = {RequestMethod.GET})
    public List<UserInfoModel> infoList(@RequestParam(required = false) String search, @RequestParam Integer page, @RequestParam Integer size) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            List<DbUser> dbUsers;
            if (search != null && !search.isEmpty()) {
                if (page == null || size == null)
                    dbUsers = ObjectSelect.query(DbUser.class).
                            where(DbUser.DELETED_DATE.isNull())
                            .and(DbUser.NAME.containsIgnoreCase(search.toLowerCase()))
                            .or(DbUser.EMAIL.containsIgnoreCase(search.toLowerCase()))
                            .or(DbUser.PHONE.containsIgnoreCase(search.toLowerCase()))
                            .pageSize(30)
                            .select(objectContext);
                else {
                    dbUsers = ObjectSelect.query(DbUser.class).
                            where(DbUser.DELETED_DATE.isNull())
                            .and(DbUser.NAME.containsIgnoreCase(search.toLowerCase()))
                            .or(DbUser.EMAIL.containsIgnoreCase(search.toLowerCase()))
                            .or(DbUser.PHONE.containsIgnoreCase(search.toLowerCase()))
                            .pageSize(size)
                            .select(objectContext);
                    if (dbUsers.size() >= size * page)
                        dbUsers = dbUsers.subList(page * size - size, page * size);
                    else if (dbUsers.size() >= size * (page - 1))
                        dbUsers = dbUsers.subList(page * size - size, dbUsers.size() - 1);
                }
            } else {
                if (page == null || size == null)
                    dbUsers = ObjectSelect.query(DbUser.class).
                            where(DbUser.DELETED_DATE.isNull())
                            .pageSize(30)
                            .select(objectContext);
                else {
                    dbUsers = ObjectSelect.query(DbUser.class).
                            where(DbUser.DELETED_DATE.isNull())
                            .pageSize(size)
                            .select(objectContext);
                    if (dbUsers.size() >= size * page)
                        dbUsers = dbUsers.subList(page * size - size, page * size);
                    else if (dbUsers.size() >= size * (page - 1))
                        dbUsers = dbUsers.subList(page * size - size, dbUsers.size() - 1);
                }
            }
            List<Long> ids = new ArrayList<>();
            dbUsers.forEach(dbUser -> {
                ids.add((Long) dbUser.getObjectId().getIdSnapshot().get("id"));
            });
            HashMap<String, LocalDateTime> articleLast = articleFunctionsService.getLastView(ids);
            HashMap<String, Integer> articleCount = articleFunctionsService.getViewCount(ids);
            HashMap<String, LocalDateTime> videoLast = videoService.getLastView(ids);
            HashMap<String, Integer> videoCount = videoService.getViewCount(ids);

            List<UserInfoModel> users = new ArrayList<>();
            dbUsers.forEach(dbUser -> {
                String userId = dbUser.getObjectId().getIdSnapshot().get("id").toString();
                users.add(UserInfoModel.builder()
                        .id(Long.valueOf(userId))
                        .email(dbUser.getEmail())
                        .name(dbUser.getName())
                        .phone(dbUser.getPhone())
                        .birthday(dbUser.getBirthday())
                        .city(dbUser.getCity())
                        .country(dbUser.getCountry())
                        .isSubscribe(dbUser.isIsSubscribe())
                        .subscriptionDate(dbUser.getSubscriptionDate())
                        .subType(dbUser.getSubType())
                        .renew(dbUser.isRenew())
                        .createdDate(dbUser.getCreatedDate())
                        .modifiedDate(dbUser.getModifiedDate())
                        .lastArticleView(articleLast.get(userId))
                        .lastVideoView(videoLast.get(userId))
                        .articleViewCount(articleCount.get(userId))
                        .videoViewCount(videoCount.get(userId))
                        .lastLoginDate(dbUser.getUserToSession().size() > 0 ? dbUser.getUserToSession().get(dbUser.getUserToSession().size() - 1).getCreatedDate() : null)
                        .build());
            });

            return users;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }


    private boolean checkEmailExists(String email) {
        try {
            ObjectContext objectContext = databaseService.getContext();
            DbUser user = ObjectSelect.query(DbUser.class)
                    .where(DbUser.EMAIL.lower().eq(email.toLowerCase()))
                    .selectFirst(objectContext);

            return user != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return true;
        }
    }

}
