package com.thelak.core.interfaces;

import com.thelak.core.models.UserInfo;

public interface ITokenService {

    String generateToken(UserInfo user);

    UserInfo parseToken(String token);
}
