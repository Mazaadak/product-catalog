package com.mazadak.product_catalog.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class IdempotencyUtil {

    public static String calculateHash(Object obj) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = obj.toString();
            byte[] hash = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
