package com.thelak.auth.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.security.SecureRandom;

@Component
public class PasswordHelper {

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.encodeBase64String(bytes);
    }

    public static String hashPassword(String value, String salt) {
        String password = salt + value;
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }
}
