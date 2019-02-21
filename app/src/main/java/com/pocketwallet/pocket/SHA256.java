package com.pocketwallet.pocket;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    public static String hashSHA256(String input) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] messageDigest = md.digest(input.getBytes());
                BigInteger no = new BigInteger(1, messageDigest);
                String hashText = no.toString(16);
                while(hashText.length() < 32) {
                    hashText = "0" + hashText;
                }
                System.out.println("HashText: " + hashText);
                return hashText;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        return null;
    }
}
