package com.thelak.route.auth.services;

import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.auth.models.*;
import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.models.subscription.SetSubscriptionModel;
import com.thelak.route.smtp.models.EmailAllRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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
    public ResponseEntity login(AuthLoginRequest request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_LOGIN), request, ResponseEntity.class).getBody());
    }

    @Override
    public ResponseEntity refresh() throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(AUTH_REFRESH), ResponseEntity.class).getBody());
    }

    @Override
    public Boolean restorePasswordRequest(String email) throws MicroServiceException {
        return null;
    }

    @Override
    public Boolean restorePasswordConfirm(RestorePasswordRequest request) throws MicroServiceException {
        return null;
    }

    @Override
    public VueHelpModel updateUser(UpdateUserModel user) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_USER_UPDATE), user, VueHelpModel.class).getBody());
    }

    @Override
    public VueHelpModel setSubscription(SetSubscriptionModel setSubscriptionModel) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_USER_SUBSCRIPTION), setSubscriptionModel, VueHelpModel.class).getBody());
    }

    @Override
    public NotificationModel getNotificationInfo() throws MicroServiceException {
        return null;
    }

    @Override
    public VueHelpModel getByEmail(String email) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(AUTH_USER_BY_EMAIL))
                .queryParam("email", email);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), VueHelpModel.class, email).getBody());
    }

    @Override
    public NotificationModel updateNotificationInfo(NotificationModel notificationModel) throws MicroServiceException {
        return null;
    }

    @Override
    public Boolean sendNotificationEmail(EmailAllRequest request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(AUTH_USER_NOTIFICATION_EMAIL), request, Boolean.class).getBody());
    }

    @Override
    public List<UserInfoModel> infoList(String search, Integer page, Integer size, DateSortEnum sort) throws MicroServiceException {
        return null;
    }
}
