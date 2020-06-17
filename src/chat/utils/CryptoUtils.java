package chat.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptoUtils {
    public static String getHex(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for(byte pByte : bytes){
            String hex = Integer.toHexString(0xff & pByte);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String getHexBase64(String password) throws NoSuchAlgorithmException {

        String passHex = getHex(password);
        byte[] base64passHex = Base64.getEncoder().encode(passHex.getBytes());

        return new String(base64passHex);
    }
}
