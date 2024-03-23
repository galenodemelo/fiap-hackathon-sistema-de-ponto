package br.com.fiap.util;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;

public class SecurityUtils {

    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
