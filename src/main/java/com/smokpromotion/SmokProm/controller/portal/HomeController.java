package com.smokpromotion.SmokProm.controller.portal;

import com.smokpromotion.SmokProm.domain.dto.EmailLanguage;
import com.smokpromotion.SmokProm.domain.entity.AdminUser;
import com.smokpromotion.SmokProm.domain.entity.DE_EmailTemplate;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.form.UserForm;
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

import javax.validation.Valid;

@Profile("smok_app")
@Controller
public class HomeController extends PortalBaseController {

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
    public String signup(Model model, @Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult) throws UserNotFoundException, NotLoggedInException
    {
       // validateTemplate(template, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("languages", EmailLanguage.values());
            model.addAttribute("userForm", userForm);
            return PUBBASE+"signup";
        }
        S_User user = new S_User(userForm);

        userService.create(user, userForm.getPassword());
        return "redirect:/login";
    }

    @PostMapping("/signup-reseller")
    public String signupReseller(Model model, @Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult) throws UserNotFoundException, NotLoggedInException
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
        return "redirect:/login";
    }

    @PostMapping("/signup-from-reseller")
    public String signupFromReseller(Model model, @Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult) throws UserNotFoundException, NotLoggedInException
    {
        // vaateTemplate(template, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("languages", EmailLanguage.values());
            model.addAttribute("userForm", userForm);
            return PUBBASE+"signup";
        }
        S_User user = new S_User(userForm);

        userService.create(user, userForm.getPassword());
        return "redirect:/login";
    }
}
