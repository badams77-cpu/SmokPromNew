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


    @RequestMapping("/login")
    public String login(Model m, Authentication auth)
    {
        return PUBBASE+"login.html";
    }

    @GetMapping("/admin/signup")
    public String signup(Model m, Authentication auth) throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);
        return PRIBASE+"signup.html";
    }

    @PostMapping("/admin/signup")
    public String signup(Model model, @Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult) throws UserNotFoundException, NotLoggedInException
    {
       // validateTemplate(template, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("languages", EmailLanguage.values());
            model.addAttribute("userForm", userForm);
            return PRIBASE+"signup";
        }
        S_User user = new S_User();

        userService.create(user, userForm.getPassword());
        return PRIBASE+"/index.html";
    }
}
