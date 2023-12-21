package org.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class InfoHash {
    public static byte[] getInfoHash(byte[] infoHash){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            return messageDigest.digest(infoHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
