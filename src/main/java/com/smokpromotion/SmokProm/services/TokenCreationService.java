package com.smokpromotion.SmokProm.services;



import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.entity.UserLoginActivity;
import com.smokpromotion.SmokProm.domain.repo.REP_UserLoginActivity;
import com.smokpromotion.SmokProm.util.GenericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Profile({"smok_app","smok-admin"})
@Service
public class TokenCreationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenCreationService.class);

    @Autowired
    REP_UserLoginActivity userLoginActivity;

    public String createToken(S_User user) throws Exception {
        UUID random = UUID.randomUUID();
        UserLoginActivity userActivityRecover = null;


        Optional<UserLoginActivity> userActivityRecoverOpt = userLoginActivity.findByUserIdForPasswordRecovery( user.getId());
        if (userActivityRecoverOpt.isPresent()){
            userLoginActivity.reset(userActivityRecoverOpt.get().id);
        }
//        if (!userActivityRecoverOpt.isPresent()){
//            userActivityRecover =new UserLoginActivity();
//            userActivityRecover.setUserId(user.getId());
//            int id = userLoginActivity.create(userActivityRecover);
//            userActivityRecover.setId(id);
//        } else {
//            userActivityRecover = userActivityRecoverOpt.get();
//        }
        String hashedBCrypt="";
 //       if (!GenericUtils.isNull(userActivityRecover)) {
            String toBeHashed = user.getUsername() + user.getId() + random + LocalDateTime.now();
            hashedBCrypt = SecurityTokenManager.encode(toBeHashed);
            userActivityRecover = new UserLoginActivity();
            userActivityRecover.setLocked(true);
            userActivityRecover.setUserId(user.getId());
            userActivityRecover.setTokenDate(LocalDateTime.now());
            String md5Hashed = SecurityTokenManager.encodeHash(hashedBCrypt);
            userActivityRecover.setToken(md5Hashed);
            LOGGER.info("Updating "+userLoginActivity);
            userLoginActivity.create( userActivityRecover);
//        } else {
//            LOGGER.debug("Admin User attempting to recover a password on an email not present on MPC admin_user table, INPUT=" + user.getUsername());
//        }
        return hashedBCrypt;
    }


}