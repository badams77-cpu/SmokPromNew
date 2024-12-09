package com.smokpromotion.SmokProm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;


@Lazy
@Component
public class SecurityTokenManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityTokenManager.class);


    public static String encode(String in) throws Exception {
        String out ="";
        String method = "encode(..) - ";
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        try {
            out = encoder.encode(in);

        } catch (Exception e) {
            LOGGER.error(method+ "An error occurred: input:"+in);
            throw e;
        }

        return out;
    }

    public static String encodeHash(String in) throws Exception {
        String method = "encodeHash(...) - ";
        String ret = "";

        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");

            md.update(in.getBytes());
            byte[] digest = md.digest();
            ret = DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (Exception e) {
            LOGGER.error(method+ "An error occurred: input:"+in);
            throw e;
        }

        return ret;

    }

}