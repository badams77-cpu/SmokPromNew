package com.smokpromotion.SmokProm.controller.portal;

import com.smokpromotion.SmokProm.domain.entity.*;
import com.smokpromotion.SmokProm.domain.repo.*;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.TwitterSearchNotFoundException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import javax.validation.Valid;

import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;

@Controller
public class MessagesController extends PortalBaseController{

    private static Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(SearchController.class);

    private static int adminId;

    private static final String adminEmail="vapidpromotions@gmail.com";


    @Autowired
    private REP_VPMessage messageRepo;

    @Autowired
    public MessagesController(REP_UserService userService) {
        try {
            setUserService(userService);
            S_User adminUser = getAdminUser(adminEmail);
            adminId = adminUser.getId();
        } catch (UserNotFoundException e){}
    }

    @RequestMapping("/a/message-home")
    public String messageHome(Model m, Authentication auth) throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);

        List<VPMessage> messages = messageRepo.findByUser(user.getId());

        m.addAttribute("messages",messages);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());

        m.addAttribute("toId", adminId);

        m.addAttribute("fromId", user.getId());

        return PRIBASE+"messages";
    }








    @RequestMapping(path="/a/message-add-post", method= RequestMethod.POST)
    public String searchAddPost(@Valid VPMessage vpMessageForm, BindingResult bindingResult, Model m, Authentication auth) throws TwitterSearchNotFoundException, UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);

        if (bindingResult.hasErrors()) {
            LOGGER.warn("WARNING Search form binding ",bindingResult.getFieldErrors());
            return "search-form-add";
        }

     //   twitterSearchForm.setUserId(user.getId());

        int newId = messageRepo.create(vpMessageForm);

        if (newId==0){
            LOGGER.warn("WARNING *** NEW SEARCH WAS NOT SAVED");
        }

        m.addAttribute("form", vpMessageForm);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());



        return "redirect:/a/messages";
    }


}
