package com.smokpromotion.SmokProm.util;


import com.smokpromotion.SmokProm.config.Md5PasswordEncoder;
import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class PwCryptUtil {

    private final static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(PwCryptUtil.class);

    public final static int MD5Num = SecVnEnum.MD5Num;

    public final static int BCryptNum =  SecVnEnum.BCryptNum;

    BCryptPasswordEncoder bCryptEncoder;

    Md5PasswordEncoder md5Encoder;


    public PwCryptUtil(){
        bCryptEncoder = new BCryptPasswordEncoder();
        md5Encoder = new Md5PasswordEncoder();
    }

    public String getPasswd(String uncrypted, int secVn){
        switch(SecVnEnum.getFromCode(secVn)){
            case MD5:
                return md5Encoder.encode(uncrypted);
            case BCRYPT:
                return bCryptEncoder.encode(uncrypted);
            default:
                return md5Encoder.encode(uncrypted);

        }
    }

    public boolean isPasswordGood(int secVn, String username, String testPasswd, String password) {
        boolean passwordGood = false;
        switch(SecVnEnum.getFromCode(secVn)){
            case MD5:
                String md5 = md5Encoder.encode(testPasswd);
                passwordGood = md5.equalsIgnoreCase(password);
                break;
            case BCRYPT:
                BCryptPasswordEncoder bencoder = new BCryptPasswordEncoder();
                passwordGood = bCryptEncoder.matches(testPasswd, password);
                break;
            default:
                LOGGER.error(username+" try to login but has unknown secVN: "+secVn);
        }
        return passwordGood;
    }


}
