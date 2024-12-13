package com.smokpromotion.SmokProm.controller.portal;

import com.smokpromotion.SmokProm.config.portal.PortalEmailConfig;
import com.smokpromotion.SmokProm.domain.dto.EmailLanguage;
import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import com.smokpromotion.SmokProm.domain.entity.DE_EmailTemplate;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.email.SmtpQueue;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.form.UserForm;
import com.smokpromotion.SmokProm.services.TokenCreationService;
import com.smokpromotion.SmokProm.util.GenericUtils;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Profile("smok_app")
@Controller
public class HomeController extends PortalBaseController {

    private static final String VAPID_LOGO="images/vapid-440x350.png";

    private static Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(HomeController.class);

    @Autowired
    private PortalEmailConfig portalEmailConfig;

    @Autowired private TokenCreationService adminTokenCreationService;

    @Autowired private SmtpQueue smtpMailSender;

    @Autowired
    private REP_UserService userService;

    @RequestMapping("/a/home")
    public String home(Model m, Authentication auth) throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);
        return PRIBASE+"home.html";
    }

    @RequestMapping("/")
    public String pubhome(Model mh)
    {
        return PUBBASE+"index.html";
    }

    @RequestMapping("/forgot")
    public String forgotpass(Model mh)
    {
        mh.addAttribute("forgottenPasswordMsg","");
        return PUBBASE+"forgot.html";
    }

    @RequestMapping("/forgot1")
    public String forgotpass1(Model mh)
    {
        return PUBBASE+"forgot";
    }

    @RequestMapping("/splash")
    public String sphome(Model mh)
    {
        return PUBBASE+"splash.html";
    }



    @RequestMapping("/login")
    public String login(Model m, Authentication auth)
    {
        return PUBBASE+"login.html";
    }

    @GetMapping("/signup")
    public String signup(Model m) throws UserNotFoundException, NotLoggedInException
    {
//        S_User user = getAuthUser(auth);
        return PUBBASE+"signup.html";
    }

    @GetMapping("/resell")
    public String reseller(Model m) throws UserNotFoundException, NotLoggedInException
    {
//        S_User user = getAuthUser(auth);
        return PUBBASE+"resell.html";
    }

    @GetMapping("/signup-reseller")
    public String signupReseller(Model m) throws UserNotFoundException, NotLoggedInException
    {
//        S_User user = getAuthUser(auth);
        return PUBBASE+"signup-reseller.html";
    }

    @GetMapping("/signup-from-reseller")
    public String signupFromReseller(Model m) throws UserNotFoundException, NotLoggedInException
    {
//        S_User user = getAuthUser(auth);
        return PUBBASE+"signup-from-reseller.html";
    }


    @PostMapping("/signup")
    public String signup(Model model, HttpServletRequest request, @Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult) throws UserNotFoundException, NotLoggedInException
    {
       // validateTemplate(template, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("languages", EmailLanguage.values());
            model.addAttribute("userForm", userForm);
            return PUBBASE+"signup";
        }
        S_User user = new S_User(userForm);

        userService.create(user, userForm.getPassword());
        try {
            String hashedBCrypt = adminTokenCreationService.createToken( user);
            String emailBody = generateMessageBodySignup(hashedBCrypt, user,request);
            smtpMailSender.send(portalEmailConfig.getMpcMailFromAddr(), portalEmailConfig.getMpcMailFromName(), user.getUsername(), "Vapid Promotions - confirm your email", emailBody);
        } catch (Exception e){
            LOGGER.warn("Exception sending signup email",e);
        }
        return PUBBASE+"/signup-confirfm";
    }



    @PostMapping("/signup-reseller")
    public String signupReseller(Model model,  HttpServletRequest request,@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult) throws UserNotFoundException, NotLoggedInException
    {
        // vaateTemplate(template, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("languages", EmailLanguage.values());
            model.addAttribute("userForm", userForm);
            return PUBBASE+"signup";
        }
        S_User user = new S_User(userForm);
        user.setResellerName("isa_reseller");

        userService.create(user, userForm.getPassword());
        try {
            String hashedBCrypt = adminTokenCreationService.createToken( user);
            String emailBody = generateMessageBodySignup(hashedBCrypt, user,request);
            smtpMailSender.send(portalEmailConfig.getMpcMailFromAddr(), portalEmailConfig.getMpcMailFromName(), user.getUsername(), "Vapid promotions - confirm your email", emailBody);
        } catch (Exception e){
            LOGGER.warn("Exception sending signup email",e);
        }
        return PUBBASE+"/signup-cofirm";
    }

    @PostMapping("/signup-from-reseller")
    public String signupFromReseller(Model model, HttpServletRequest request, @Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult) throws UserNotFoundException, NotLoggedInException
    {
        // vaateTemplate(template, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("languages", EmailLanguage.values());
            model.addAttribute("userForm", userForm);
            return PUBBASE+"signup";
        }
        S_User user = new S_User(userForm);

        userService.create(user, userForm.getPassword());
        try {
            String hashedBCrypt = adminTokenCreationService.createToken( user);
            String emailBody = generateMessageBodySignup(hashedBCrypt, user,request);
            smtpMailSender.send(portalEmailConfig.getMpcMailFromAddr(), portalEmailConfig.getMpcMailFromName(), user.getUsername(), "Vapid Promotions confirm your email", emailBody);
        } catch (Exception e){
            LOGGER.warn("Exception sending signup email",e);
        }
        return PUBBASE+"/signup-cofirm";
    }


    private String generateMessageBodySignup(String hashed, S_User user, HttpServletRequest request ) {
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
                    "Thank you for signing up to Vapid Promotions<p/>"+
                    "<p>To continue confirm your email by clicking the link below: </p>"+
                    "<a href='"+url+"/signup-confirm?pr="+hashed+"' mc:disable-tracking  > Click here to create a new password </a></p>"+
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
}
