package com.smokpromotion.SmokProm.config.admin;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(value = {"admin", "dxpulse_admin"})
@Service
public class AdminMajoranaLoginAttemptService {

/*


    @Autowired private HttpServletRequest request;
    @Autowired private DR_AdminUserLoginActivity userLoginActivity;
    @Autowired  private REP_AdminUserService legacyMajoranaUserService;
    @Autowired DA_AdminUserLoginActivity daUserLoginActivity;


    private static final String NULL_BLANK_USERNAME_FOR_LOG = "UNKNOWN";

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminMajoranaLoginAttemptService.class);

    @Value("${Majorana.lockout.period.minutes}")
    private int numberOfMinutesLockedOut;

    @Value("${Majorana.max.login.attempts}")
    private int MAX_ATTEMPT;

    @Value("${Majorana.max.login.lockouts:1}")
    private int MAX_NUM_LOCKOUTS;

//    private LoadingCache<String, Integer> bruteForceAttackCache;


    public AdminMajoranaLoginAttemptService() {
        super();

    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }



    public void setNumberOfMinutesLockedOut(int numberOfMinutesLockedOut) {
        this.numberOfMinutesLockedOut = numberOfMinutesLockedOut;
    }

    public void setMAX_ATTEMPT(int MAX_ATTEMPT) {
        this.MAX_ATTEMPT = MAX_ATTEMPT;
    }

    public void loginSucceeded(String key) {
        String method = "loginSucceeded(...) - ";
        DE_AdminUser user = legacyMajoranaUserService.getUser(key);
        if (user!=null) {
            loginSucceeded(user);
        } else {
            LOGGER.error("loginSucceed - no user found form username: " + ((key==null || key.equals(""))?
                    NULL_BLANK_USERNAME_FOR_LOG: key));
        }
    }

     public void loginSucceeded(DE_AdminUser user){
        if (user!=null ){
            PortalEnum legacyMajoranaPortalEnum =   PortalEnum.AWS ;
            userLoginActivity.reset(legacyMajoranaPortalEnum, user, true);
            LocalDateTime date = LocalDateTime.now();
            if (legacyMajoranaUserService.updateLastLogin(legacyMajoranaPortalEnum, user.getUserid(), date)){
                LOGGER.debug("User "+user.getUsername()+" update last login date to " + date);
            }
        } else {
            LOGGER.error("loginSucceeded - no user specified ");
        }
    }


 
    public void loginFailed(String key) {
        String method = "loginFailed(...) - ";

        DE_AdminUser user = legacyMajoranaUserService.getUser(key);
        if (user != null) {
            loginFailed(user);
        } else {
            LOGGER.error("loginFailed - no user found for username:" + ((key==null || key.equals(""))?
                    NULL_BLANK_USERNAME_FOR_LOG: key));
        }
    }

    public void loginFailed(DE_AdminUser user) {
        if (user != null) {
            PortalEnum legacyMajoranaPortalEnum = (PortalEnum.AWS);


            DE_UserLoginActivity loginActivity = userLoginActivity.findOrAddNewByUserId(legacyMajoranaPortalEnum, user);
            if (loginActivity == null) {
                LOGGER.error("loginFailed: No user login activity record retrieved for user: " + user.getUserid());
                return;
            }
            if (loginActivity.getIsLocked() == 0) {
                int attempt = loginActivity.getFailedAttempt();
                attempt++;
                loginActivity.setFailedAttempt(attempt);
                loginActivity.setLastAttemptDate(LocalDateTime.now());
                LOGGER.debug("loginFailed: user authentication failed attempt " + attempt + " / " + MAX_ATTEMPT + " for username: " + user.getUsername() + " userid: " + user.getUserid());
                if (attempt >= MAX_ATTEMPT) {
                    // lock this user now, due to too many failed attempts
                    loginActivity.setIsLocked(1);
                    loginActivity.setBlockedCount(loginActivity.getBlockedCount() + 1);
                    userLoginActivity.update(legacyMajoranaPortalEnum, loginActivity);
                    LOGGER.error("loginFailed: user authentication failed " + MAX_ATTEMPT + " times - locking username: " + user.getUsername() + " userid: " + user.getUserid() +
                            " lockout " + loginActivity.getBlockedCount() + " / " + MAX_NUM_LOCKOUTS);
                    if (loginActivity.getBlockedCount() >= MAX_NUM_LOCKOUTS) {
                        LOGGER.error("loginFailed: user lockout occurred " + MAX_NUM_LOCKOUTS + " times - account will not be unlocked automatically - username: " + user.getUsername() + " userid: " + user.getUserid());
                    }
                }
                userLoginActivity.update(legacyMajoranaPortalEnum, loginActivity);
            } else {
                LOGGER.error("loginFailed - no user specified ");
            }
        }
    }



//    private void printConfigurationError() {0
//        LOGGER.error("*****************************************************");
//        LOGGER.error("*** TABLES FOR THE USER LOGIN ACTIVITY NOT EXIST ****");
//        LOGGER.error("*RUN THE DEVELOPMENT COMPONENT FOR UserLoginActivity*");
//        LOGGER.error("*****************************************************");
//        LOGGER.error("*****************************************************");
//    }


    public void checkIfBlockedRaiseAuthError(String key) throws AuthenticationFailedException {
        DE_AdminUser user = legacyMajoranaUserService.getUser(key);
        if (user != null) {
            checkIfBlockedRaiseAuthError(user);
        } else {
            LOGGER.error("checkIfBlockedRaiseAuthError - no user found for username: " + GenericUtils.ifNullOrEmpty(key,NULL_BLANK_USERNAME_FOR_LOG));
        }
    }

    public boolean checkIfBlockedRaiseAuthError(DE_AdminUser user) throws  AuthenticationFailedException{


        boolean ret = false;
        try {

            if (user != null) {
                PortalEnum legacyMajoranaPortalEnum = PortalEnum.AWS;

                DE_UserLoginActivity loginActivity = userLoginActivity.findOrAddNewByUserId(legacyMajoranaPortalEnum, user);

                if (loginActivity != null) {
                    if (GenericUtils.isValid(loginActivity.getToken())) {
                        LOGGER.debug("checkIfBlockedRaiseAuthError - User is trying to login after a Forgot Password in progress - login blocked - login activity id: " + loginActivity.getId());
                        throw new AuthenticationFailedException(AuthenticationFailureReasonEnum.BLOCKED_PWD_RECOVERY, "Password recovery in progress");
                    } else if (loginActivity.getFailedAttempt() >= MAX_ATTEMPT || loginActivity.getIsLocked() == 1) {
                        //account locked, check if the lock is expired
                        if (isUnlockExpired(loginActivity)) {
                            userLoginActivity.reset(legacyMajoranaPortalEnum, user, false);
                        } else {
                            LOGGER.debug("checkIfBlockedRaiseAuthError - User is trying to login after too many invalid attempt - login blocked - login activity id: " + loginActivity.getId());
                            throw new AuthenticationFailedException(AuthenticationFailureReasonEnum.BLOCKED_AFTER_INVALID_ATTEMPTS, "Too many invalid login attempts");
                        }
                    }

                }
            }

        } catch (AuthenticationFailedException e){
            throw e;
        } catch (Exception e) {
            LOGGER.error("checkIfBlockedRaiseAuthError - an error occurred - userid: " + (user!=null ? user.getUserid() : "UNKNOWN") + e.getMessage());
        }


//        LOGGER.debug(method+" User Blocked: "+ret);

        return ret;
    }

    public boolean isPasswordRecoveryInProgress(String key) {
        boolean ret = false;
        DE_AdminUser user = legacyMajoranaUserService.getUser(key);

        if (GenericUtils.isValid(key)) {
            try {
                if (user != null){

                        DE_UserLoginActivity loginActivity =  userLoginActivity.findOrAddNewByUserId(PortalEnum.AWS, user);
                        if (loginActivity != null && GenericUtils.isValid(loginActivity.getToken())) {
                            ret = true;
                        }
                }
            } catch (Exception e) {
                LOGGER.error("isPasswordRecoveryInProgress - ERROR for username " + (GenericUtils.isNullOrEmpty(key)? key:"UNKNOWN") + ": " + e.getMessage());
                e.printStackTrace();
            }


        }
        return ret;

    }

    protected boolean isUnlockExpired(DE_UserLoginActivity loginActivity) {
        boolean ret = false;

        if (loginActivity != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lockedInstant = loginActivity.getLastAttemptDate();
            lockedInstant = lockedInstant.plusMinutes(numberOfMinutesLockedOut);
            // URC-1871 increase locked out time by number of locked counts
            int lockoutDelayMins = numberOfMinutesLockedOut*(loginActivity.getBlockedCount()+1);

            lockedInstant = lockedInstant.plusMinutes(lockoutDelayMins);
            if (lockedInstant.isEqual(now) || lockedInstant.isBefore(now)) {
                if (loginActivity.getBlockedCount() >= MAX_NUM_LOCKOUTS) {
                    LOGGER.debug("isUnlockExpired: blocked count exceeded");
                    return false ;
                }
                LOGGER.debug("isUnlockExpired - last attempt: " + lockedInstant + " more than " + lockoutDelayMins + " minutes ago, unlocking ");
                ret = true;
            }
        }
        return ret;
    }

*/










}