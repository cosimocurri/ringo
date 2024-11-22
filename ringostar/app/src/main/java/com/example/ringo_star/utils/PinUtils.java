package com.example.ringo_star.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;

import java.util.Base64;

public class PinUtils {
    public static String hashPin(String pin, byte[] salt) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashedPin = md.digest(pin.getBytes());
        return Base64.getEncoder().encodeToString(hashedPin);
    }

    public static byte[] generateSalt() throws Exception {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}