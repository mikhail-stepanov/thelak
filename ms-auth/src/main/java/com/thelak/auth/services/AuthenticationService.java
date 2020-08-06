package com.thelak.auth.services;

import com.thelak.auth.endpoints.AuthenticationEndpoint;
import com.thelak.auth.util.PasswordHelper;
import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.core.interfaces.ITokenService;
import com.thelak.core.models.UserInfo;
import com.thelak.core.util.RLUCache;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbUser;
import com.thelak.database.entity.DbUserSession;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.auth.models.AuthInfoRequest;
import com.thelak.route.auth.models.AuthLoginRequest;
import com.thelak.route.auth.models.AuthSignupRequest;
import com.thelak.route.auth.models.UserModel;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsBadRequestException;
import com.thelak.route.exceptions.MsNotAuthorizedException;
import com.thelak.route.exceptions.MsObjectNotFoundException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
public class AuthenticationService extends AbstractMicroservice implements IAuthenticationService {

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
    public UserModel info(AuthInfoRequest request) throws MicroServiceException {
        try {
            DbUser user = SelectById.query(DbUser.class, request.getId()).selectFirst(objectContext);

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
            throw new MsObjectNotFoundException("Exception while finding user with id: ", Long.toString(request.getId()));
        }
    }

    @Override
    public UserModel signUp(AuthSignupRequest request) throws MicroServiceException {
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
    public String login(AuthLoginRequest request) throws MicroServiceException {
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
