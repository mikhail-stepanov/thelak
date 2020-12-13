package com.thelak.route.auth.interfaces;

import com.thelak.route.auth.models.*;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.models.subscription.SetSubscriptionModel;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;

public interface IAuthenticationService {

    String AUTH_INFO = "/v1/auth/info";
    String AUTH_SIGN_UP = "/v1/auth/signup";
    String AUTH_LOGIN = "/v1/auth/login";
    String AUTH_REFRESH = "/v1/auth/refresh";
    String AUTH_USER_UPDATE = "/v1/auth/update";
    String AUTH_USER_RESTORE_REQUEST = "/v1/auth/restore/request";
    String AUTH_USER_RESTORE_CONFIRM = "/v1/auth/restore/confirm";
    String AUTH_USER_SUBSCRIPTION = "/v1/auth/subscription";

    String AUTH_USER_INFO = "/v1/auth/info/list";

    String AUTH_USER_NOTIFICATION_INFO = "/v1/auth/notification/info";
    String AUTH_USER_NOTIFICATION_UPDATE = "/v1/auth/notification/update";


    VueHelpModel info() throws MicroServiceException;

    VueHelpModel signUp(AuthSignupRequest request) throws MicroServiceException, ParseException;

    ResponseEntity login(AuthLoginRequest request) throws MicroServiceException;

    ResponseEntity refresh() throws MicroServiceException;

    Boolean restorePasswordRequest(String email) throws MicroServiceException;

    Boolean restorePasswordConfirm(RestorePasswordRequest request) throws MicroServiceException;

    VueHelpModel updateUser(UpdateUserModel user) throws MicroServiceException;

    VueHelpModel setSubscription(SetSubscriptionModel setSubscrip) throws MicroServiceException;

    NotificationModel getNotificationInfo() throws MicroServiceException;

    NotificationModel updateNotificationInfo(NotificationModel notificationModel) throws MicroServiceException;

    List<UserInfoModel> infoList(String search, Integer page, Integer size) throws MicroServiceException;
}
