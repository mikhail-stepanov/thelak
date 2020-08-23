package com.thelak.core.interfaces;

import com.thelak.core.models.UserInfo;
import com.thelak.route.exceptions.MsNotAuthorizedException;

public interface ITokenService {

    String generateToken(UserInfo user);

    UserInfo parseToken(String token) throws MsNotAuthorizedException;
}
