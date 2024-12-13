package com.smokpromotion.SmokProm.controller.portal;


import com.smokpromotion.SmokProm.config.portal.PortalEmailConfig;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.entity.UserLoginActivity;
import com.smokpromotion.SmokProm.domain.repo.REP_UserLoginActivity;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.email.SmtpQueue;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.form.ChangeForgottenPasswordFormValidator;
import com.smokpromotion.SmokProm.form.ChangePasswordForm;
import com.smokpromotion.SmokProm.services.SecurityTokenManager;
import com.smokpromotion.SmokProm.services.TokenCreationService;
import com.smokpromotion.SmokProm.util.GenericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;


@Controller
@Profile({"smok_app"})
@SessionAttributes({
        "userActivity",
        "user"})
@RequestMapping(path="/")
public class PasswordRecoveryController extends PortalBaseController {

    private static final String VAPID_LOGO="images/vapid-440x350.png";

    @Autowired
    PortalEmailConfig portalEmailConfig;

    @Value("${vapid.password.recovery.token.expires.after.minutes:15}")
    private int recoveryTokenValidityMinutes;


    @Autowired private SmtpQueue smtpMailSender;
    @Autowired private REP_UserService userService;
    @Autowired private REP_UserLoginActivity adminUserLoginActivity;
    @Autowired private ChangeForgottenPasswordFormValidator changePasswordFormValidator;
    @Autowired private MessageSource messageSource;
    @Autowired private TokenCreationService adminTokenCreationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordRecoveryController.class);


    @RequestMapping(path = "/prec", method = {RequestMethod.GET})
    public String get(
            HttpServletRequest request,
            Model model) {
        LOGGER.debug("/prec executed via GET, redirecting to login.");
        return getLoginTemplatePath();
    }

    /***
     * Entry point for the forgotten password function
     * This method generate a token and send an email to the user.
     * If no user is found, the system return anyway a message like if tsehe process has been started.
     * @param request
     * @param username
     * @param model
     * @return
     */
    @RequestMapping(path = "/prec", method = {RequestMethod.POST})
    public String postPrec(
            HttpServletRequest request,
            @RequestParam String username,
            Model model)  {
        String message = null;
        S_User user =null;
        try {
//            simpleUserLogging.registerEvent(SimpleUserLogger.APP_PORTAL, request, null);
            message = "An email has been sent, check your inbox and follow the instructions";
            UUID random = UUID.randomUUID();
            try {
                user = userService.findByName(username);
            } catch (UserNotFoundException e1){
                LOGGER.error("postPrec: error user not found ",e1);
                message = "An error occurred, not found, please try again.";
            }
            UserLoginActivity userActivityRecover = null;

            if (user != null && user.getUsername() != null && user.getUsername().equalsIgnoreCase(username) && user.isUseractive()) {
                try {
                    String hashedBCrypt = adminTokenCreationService.createToken( user);
                    String emailBody = generateMessageBody(hashedBCrypt, user, request);
                    smtpMailSender.send(portalEmailConfig.getMpcMailFromAddr(), portalEmailConfig.getMpcMailFromName(), username, "Forgotten Password", emailBody);
                    LOGGER.info("postPrec: password revovery initiated for "+logInfo(user));
                } catch (MessagingException e) {
                    LOGGER.error("postPrec: error "+logInfo(user)+e.getMessage());
                    message = "An error occurred, mess, please try again.";
                } catch (Exception e) {
                    LOGGER.error("postPrec: error "+logInfo(user)+e.getMessage());
                    message = "An error occurred, exec, please try again."+e.getMessage();
                }
            }

        } catch (Exception e) {
            LOGGER.error("postPrec: error "+logInfo(user)+e.getMessage());
            message = "An error occurred, please try again.";

            e.printStackTrace();
        }


        model.addAttribute("forgottenPasswordMsg", message);
        return getLoginTemplatePath();

    }

    /***
     * This method is the one triggered by the link sent to the user.
     * If the token is expired, a message will be returned
     *
     * If the checks are fine, the user will be redirect to another url (mail confirm) to hide the token
     * @param request
     * @param pr
     * @param model
     * @return
     */
    @RequestMapping(path = "/prec/reset", method = { RequestMethod.GET })
    public String recovery(

            HttpServletRequest request,
            @RequestParam(required = false) String pr,
            Model model) {


        String errorMessage = "";
        String ret = "login?error="+errorMessage;
        byte[] decodedURLBytes = Base64.getUrlDecoder().decode(pr);
        pr= new String(decodedURLBytes);
        try {

            if (GenericUtils.isValid(pr)) {
                String md5Hashed = SecurityTokenManager.encodeHash(pr);
                Optional<UserLoginActivity> userActivity = adminUserLoginActivity.findByToken( md5Hashed);

                if (!userActivity.isPresent()) {
                    LOGGER.debug("Tried to recover a password with an unknown token but:  No user found with this token (maybe expired?), redirect to login page");
                    ret = getLoginTemplatePath();
                    errorMessage = "An error occurred, no token , please click again on Forgot your Password ";
                    model.addAttribute("forgottenPasswordMsg", errorMessage);
//                } else if (userActivity.getToken() != null && userActivity.getTokenCreationDate() != null && userActivity.getTokenCreationDate().plusMinutes(numberOfMinutesLockedOut).isBefore(LocalDateTime.now())) {
                } else if (userActivity.get().getToken() != null && userActivity.get().isTokenExpired(recoveryTokenValidityMinutes)) {
                    LOGGER.debug("recovery - Tried to recover a password - a user found id="+logInfo(userActivity.get())+"with this token but token expired - redirect to login page");
                    errorMessage = "Your password recovery link has now expired. Please restart the process, by clicking on 'Forgot Your Password' again.";
                    //                errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
                    model.addAttribute("forgottenPasswordMsg", errorMessage);
                    ret = getLoginTemplatePath();
                } else if (userActivity.isPresent()) {
                    model.addAttribute("userActivity", userActivity.get());
                    ret = "redirect:/prec/confirm-email";
                    LOGGER.debug("recovery -"+logInfo(userActivity.get())+" recovering a password with a correct token and correct email");
                    LOGGER.debug("recovery - Redirect to this URL to "+ret+" hide the TOKEN from GET params");

                }
            } else {
                LOGGER.debug("recovery(...) - /prec/reset without pr parameter specified.");
                ret = getLoginTemplatePath();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        return ret;

    }

    /***
     * This method purpose is purely to do not show the token on the GET parameter.
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(path = "/prec/confirm-email", method = { RequestMethod.GET })
    public String confirmMail(
            HttpServletRequest request,
            Model model) {
        String errorMessage = "";
        String ret = "redirect: login?error="+errorMessage;
        try {
            ret = PUBBASE + "confirm_email";
        } catch (Exception e) {
            LOGGER.warn("confirmEmail: exception "+e);
            e.printStackTrace();
        }

        return ret;

    }


    /**
     * Check if the email match with the token
     * If so redirect the user to the change password screen
     * @param request
     * @param email
     * @param userActivity
     * @param model
     * @return
     */
    @RequestMapping(path = "/prec/check-email", method = { RequestMethod.POST })
    public String confirmEmailPost(
            HttpServletRequest request,
            @RequestParam String email,
            UserLoginActivity userActivity,
            Model model)  {

        // default return template - may be modified below
        String ret = getLoginTemplatePath();
        String method = "confirmEmailPost(...) - ";

        try {

            S_User user =userService.findByName(email);
            String errorMessage = "";
            userActivity = (UserLoginActivity)  request.getSession().getAttribute("userActivity");


            if(user== null || userActivity == null || userActivity.getUserId() != user.getId()) {
                LOGGER.debug(method+"User entered an email that does not match with the recovery token.");
                LOGGER.debug("User id ="+(user!=null));
                LOGGER.debug(" userActivity="+(userActivity!=null));
                if (user!=null && userActivity!=null) {LOGGER.debug("confirmEmailPost "+logInfo(userActivity)); }
//                    errorMessage  ="The username entered was not as expected. Please restart the process, by clicking on 'Forgot Your Password' again.";
                //    errorMessage  ="An error occurred. Please try again.";
                ret = getLoginTemplatePath();
            } else if (userActivity.isTokenExpired(recoveryTokenValidityMinutes))   {
                LOGGER.debug(method+"Token is expired. "+logInfo(userActivity));
                errorMessage ="Password Recovery Request is expired. Please restart the process, by clicking on 'Forgot Your Password' again ";
            }

            if (GenericUtils.isValid(errorMessage)) {
                ret = getLoginTemplatePath();
//                    model.addAttribute("forgottenPasswordMsg", errorMessage);
                if (model.containsAttribute("userActivity")) {
                    model.asMap().remove("userActivity");
                }

            } else  if (userActivity != null && user != null && userActivity.getUserId() == user.getId()) {
                LOGGER.debug("User Recovery Password confirmed "+logInfo(user)+"- proceeding to the update password");
                ChangePasswordForm changePasswordForm = new ChangePasswordForm();
                changePasswordForm.setUserId(user.getId());
                model.addAttribute("changePasswordForm", changePasswordForm);
                model.addAttribute("user", user);
                model.addAttribute("id", user.getId());
                ret = PUBBASE + "recovery_password";
            }


        } catch (Exception e) {
            LOGGER.debug(method+": error ", e);
        }

        return ret;

    }

    /**
     * Change the password of this user, is not UserLoginActivity are stored (hijacking attempt), the user will be redirected to the login page
     *
     * @param request
     * @param authentication
     * @param model
     * @param changePasswordForm
     * @param bindingResult
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(path = "/prec/change-password", method = RequestMethod.POST)
    public String post(
            HttpServletRequest request,
            Authentication authentication,
            Model model,
            ChangePasswordForm changePasswordForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        String ret = "";
        UserLoginActivity userActivity = null;
        S_User user = null;
        if (request.getSession().getAttribute("userActivity") != null) {
            userActivity = (UserLoginActivity) request.getSession().getAttribute("userActivity");
            user = (S_User) request.getSession().getAttribute("user");
            changePasswordForm.setCurrent("dummyPwdToPassValidatation");
            changePasswordForm.setUserId(user.getId());
//            changePasswordForm.setPolicy(true);
            changePasswordFormValidator.validate(changePasswordForm, bindingResult);

            if (bindingResult.hasErrors()) {

                ret = PUBBASE + "recovery_password";

            } else {

                try {
                    userService.changePassword( user.getId(), changePasswordForm.getNeww());
                    adminUserLoginActivity.reset( user.getId());
                    String emailBody = generateMessageBodyPasswordChanged(user,request);
                    smtpMailSender.send( portalEmailConfig.getMpcMailFromAddr(),portalEmailConfig.getMpcMailFromName() ,user.getUsername(), "Password Changed", emailBody);
                    LOGGER.warn("post: changed password "+logInfo(user));
                    ret = PUBBASE + "recovery_password_success";
                } catch (Exception e) {
                    LOGGER.warn("post: exception "+logInfo(user)+": "+e.toString());
                    ret = getLoginTemplatePath();

                    e.printStackTrace();
                }

            }


        }

        return ret;

    }

    private String generateMessageBody(String hashed, S_User user, HttpServletRequest request ) {
        String body = "";
        if (!GenericUtils.isNull(body)) {

            URI contextUrl = URI.create(request.getRequestURL().toString()).resolve(request.getContextPath());
            String conString = contextUrl.toString().replace(portalEmailConfig.getDefaultContext(), portalEmailConfig.getExternalContext());

            try {
                hashed = Base64.getUrlEncoder().encodeToString( hashed.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = portalEmailConfig.isUseHttps() ? conString.replace("http:", "https:") : conString;
            body+="<html><head><title></title></head><body> " +
                    "<p>Dear "+user.getFirstname()+" "+user.getLastname()+", </p>"+
                    "<p>"+
                    "We have sent you this message because you requested that your vapid promotion  password be reset. </p>"+
                    "<p>To recover access to your account, you must create a new password. </p>"+
                    "<p>"+
                    "To do this, click the link below to open a new secure web browser session. </p>"+
                    "<p>Then enter the required information and follow the instructions to set a new password. </p>"+
                    "<p>"+
                    "Reset your password: </p>"+
                    "<a href='"+url+"prec/reset?pr="+hashed+"' mc:disable-tracking  > Click here to create a new password </a></p>"+
                    "<p>" +
                    "Note that this link will expire after a short period of time. " +
                    "If you find, when clicking on the link that it has expired, please request another one, by using the 'Forgot your password' link on the  login page. " +
                    "</p>"+
                    "<p>"+
                    "Thank You.</p>"+
                    "<h3> Vapid Promotions Admin Team</h3>" +
                             "<img src='"+url.replace("/prec/reset", "") + VAPID_LOGO+"'><br>"+

                    "<br><br>"
                    + "</body>"
                    + "</html>";

        }
        return body;

    }

    private String generateMessageBodyPasswordChanged(S_User user,HttpServletRequest request ) {
        String body = "";
        if (!GenericUtils.isNull("not null")) {
            URI contextUrl = URI.create(request.getRequestURL().toString()).resolve(request.getContextPath());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
            String formatDateTime = LocalDateTime.now().format(formatter);
            String conString = contextUrl.toString().replace(portalEmailConfig.getDefaultContext(), portalEmailConfig.getExternalContext());
            String emailUrl = conString.replace("/prec/reset", "/login");
            String url = portalEmailConfig.isUseHttps() ? conString.replace("http:", "https:") : contextUrl.toString();
            if (portalEmailConfig.isUseHttps()){
                emailUrl = emailUrl.replace("http:","https:");
            }
            LOGGER.warn("emailURL: "+emailUrl);
            LOGGER.warn("url: "+url);
            body+="<html><head><title></title></head><body> " +
                    "<p>Dear "+user.getFirstname()+" "+user.getLastname()+", </p>"+

                    "<p>Your Vapid Promotions account password was changed on "+formatDateTime+" (GMT).</p>" +

                    "<p>If you did not initiate this change, please reset your Vapid Promotions password using the 'Forgot your password' link on the login page below, and contact support.</p>" +


                    "<a href='"+emailUrl+"' mc:disable-tracking> Click here to access the Vapid Promotions Login page</a>" +

                    "<p>Thank You.</p>" +
                    "<h3>Vapid Promotions Team</h3>" +
                             "<img src='"+url.replace("/prec/reset", "") + VAPID_LOGO +"'><br>"+


                    "<br><br>"
                    + "<br />"
                    + "</body>"
                    + "</html>";

        }
        return body;

    }

    /**
     * Returns the login template path
     * @return Login template path appropriate to this admin portal
     */
    private String getLoginTemplatePath() {
        return PUBBASE + "forgot";
    }

    private String logInfo(S_User user){
        return user==null? " null user ":" User="+user.getUsername()+" id="+user.getId();
    }

    private String logInfo(UserLoginActivity userAct){
        if (userAct==null){ return "null user login activity"; }
        Optional<S_User> user = userService.getOptional(userAct.getUserId());
        if (!user.isPresent()){ return "user login activity found but no user with id="+userAct.getUserId(); }
        return logInfo(user.get());
    }
}


