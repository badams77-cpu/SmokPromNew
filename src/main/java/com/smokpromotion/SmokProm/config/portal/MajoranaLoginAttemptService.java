package com.smokpromotion.SmokProm.config.portal;

import java.time.LocalDateTime;
import java.util.Optional;

import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.entity.UserLoginActivity;
import com.smokpromotion.SmokProm.domain.repo.REP_UserLoginActivity;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.util.GenericUtils;
import com.smokpromotion.SmokProm.util.PwCryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;

@Service
@Profile({"smok_app","smok_admin"})
public class MajoranaLoginAttemptService {



    @Autowired private HttpServletRequest request;
//    @Autowired private DR_UserLoginActivity userLoginActivity;
     @Autowired  private REP_UserService userService;
    @Autowired
    REP_UserLoginActivity userLoginActivity;

    private static final String NULL_BLANK_USERNAME_FOR_LOG = "UNKNOWN";

    private static final Logger LOGGER = LoggerFactory.getLogger(MajoranaLoginAttemptService.class);



    @Value("${Majorana.lockout.period.minutes}")
    private int numberOfMinutesLockedOut;

    @Value("${Majorana.max.login.attempts}")
    private int MAX_ATTEMPT;


    @Value("${Majorana.max.login.lockouts:1}")
    private int MAX_NUM_LOCKOUTS;

//    private LoadingCache<String, Integer> bruteForceAttackCache;


    public MajoranaLoginAttemptService() {
        super();

    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

//    public void setUserLoginActivity(DR_UserLoginActivity userLoginActivity) {
//        this.userLoginActivity = userLoginActivity;
//    }
//
//    public void setLegacyMajoranaUserService(DS_UserService legacyMajoranaUserService) {
//        this.legacyMajoranaUserService = legacyMajoranaUserService;
//    }
//
//    public void setDaUserLoginActivity(DA_PortalUserLoginActivity daUserLoginActivity) {
//        this.daUserLoginActivity = daUserLoginActivity;
//    }

    public void setNumberOfMinutesLockedOut(int numberOfMinutesLockedOut) {
        this.numberOfMinutesLockedOut = numberOfMinutesLockedOut;
    }

    public void setMAX_ATTEMPT(int MAX_ATTEMPT) {
        this.MAX_ATTEMPT = MAX_ATTEMPT;
    }

    public void loginSucceeded(String key)  {
        String method = "loginSucceeded(...) - ";

        S_User user = null;
        try {
            user =userService.findByName(key);
        } catch (Exception e){
            LOGGER.warn("Cound not find user",e);
        }
        if (user != null) {
            loginSucceeded(user);
        } else {
            LOGGER.error("loginSucceeded - no user found for username: " + GenericUtils.ifNullOrEmpty(key,NULL_BLANK_USERNAME_FOR_LOG));
        }
    }

    public void loginSucceeded(S_User user) {
        String method = "loginSucceeded(...) - ";
        if (user!=null){
            user.setLastVisit(LocalDateTime.now());
            LOGGER.debug("loginSucceeded - login successful for: "+user.getUsername());
            userService.update(user);

//            LocalDateTime date = LocalDateTime.now();
//            if (legacyMajoranaUserService.updateLastLogin(legacyMajoranaPortalEnum, user.getUserid(), date)){
//                LOGGER.debug("User "+user.getUsername()+" update last login date to " + date);
//            }
        } else {

            LOGGER.error("loginSucceeded - no user specified ");
        }
    }

    public void loginFailed(String key) {
        S_User user = null;
        try {
            user = userService.findByName(key);
        } catch (Exception e){}
        if (user != null) {
            loginFailed(user);
        } else {
            LOGGER.error("loginFailed - no user found for username: " + GenericUtils.ifNullOrEmpty(key, NULL_BLANK_USERNAME_FOR_LOG));
        }
    }

    public void loginFailed(S_User user) {

        if (user==null) {
            LOGGER.error("loginFailed - no user specified ");
        } else {
            LOGGER.error("loginFailed for user "+user.getUsername());
            //    PortalEnum legacyMajoranaPortalEnum = (user.portalDbIsAws() ? PortalEnum.AWS : PortalEnum.DC);
        }

//            DE_UserLoginActivity loginActivity = userLoginActivity.findOrAddNewByUserId(legacyMajoranaPortalEnum, user);
//            if (loginActivity == null) {
//                LOGGER.error("loginFailed: No user login activity record retrieved for user: " + user.getUserid());
//                return;
//            }
//            if (loginActivity.getIsLocked() == 0) {
//                int attempt = +loginActivity.getFailedAttempt();
//                attempt++;
//                loginActivity.setFailedAttempt(attempt);
//                loginActivity.setLastAttemptDate(LocalDateTime.now());
//                userLoginActivity.update(legacyMajoranaPortalEnum, loginActivity);
//                LOGGER.debug("loginFailed: user authentication failed attempt " + attempt + " / " + MAX_ATTEMPT + " for username: " + user.getUsername() + " userid: " + user.getUserid());
//                if (attempt >= MAX_ATTEMPT) {
//                    // lock this user now, due to too many failed attempts
//                    loginActivity.setIsLocked(1);
//                    loginActivity.setBlockedCount(loginActivity.getBlockedCount() + 1);
//                    userLoginActivity.update(legacyMajoranaPortalEnum, loginActivity);
//                    LOGGER.error("loginFailed: user authentication failed more than " + MAX_ATTEMPT + " times - locking account for username: " + user.getUsername() + " userid: " + user.getUserid()
//                            + " lockout " + loginActivity.getBlockedCount() + " / " + MAX_NUM_LOCKOUTS);
//                    if (loginActivity.getBlockedCount() >= MAX_NUM_LOCKOUTS) {
//                        LOGGER.error("loginFailed: user lockout occurred " + MAX_NUM_LOCKOUTS + " times - account will not be unlocked automatically - userid: " + user.getUserid());
//                    }
//                }
//            }
//        }
    }




    public boolean isPasswordRecoveryInProgress(String key) {
        boolean ret = false;
        S_User user = new S_User();
        try {
           user = userService.findByName(key);
           Optional<UserLoginActivity> ula = userLoginActivity.findByUserId(user.getId());
           if (ula.isPresent() && ula.get().isLocked()){
               return true;
           }
        } catch (Exception e){
            LOGGER.warn("Could not find user "+key);
        }

        return user.getChangePassTokenCreate()!=null &&
                user.getChangePassTokenCreate().
                        isBefore(LocalDateTime.now().plusMinutes(userService.getChangePasswordTimeOut()));

    }






    /**
     * To be used against brute force attack.
     *
     * @return
     */
    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For"); //if user is behind a proxy
        if (xfHeader == null){
            return request.getRemoteAddr();

        }
        return xfHeader.split(",")[0];
    }






//    private void addIPForPossibleBruteForce() {
//        int attempts = 0;
//        try {
//            attempts = bruteForceAttackCache.get(getClientIP());
//        } catch (ExecutionException e) {
//            attempts = 0;
//        }
//        attempts++;
//        bruteForceAttackCache.put(getClientIP(), attempts);
//    }

}