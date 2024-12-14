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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
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








    @RequestMapping(value = "/a/message-add-post", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE )
    public String searchAddPost(@RequestBody MultiValueMap<String, String> data, Model m, Authentication auth) throws Exception, UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);
        VPMessage mess = new VPMessage();
        List<String> def = new LinkedList<>();
        def.add("0");
 //       try {
            mess.setFrom((Integer.parseInt(data.getOrDefault("from",def).get(0).toString())));
            mess.setTo((Integer.parseInt(data.getOrDefault("to",def).get(0).toString())));
            mess.setMessage(data.getOrDefault("message",def).get(0).toString());
  //      } catch (Exception e){
            LOGGER.warn("WARNING Search form binding ");
  //          return PRIBASE+"messages";
  //      }

     //   twitterSearchForm.setUserId(user.getId());



        int newId = messageRepo.create(mess);

        if (newId==0){
            LOGGER.warn("WARNING *** NEW MESSAGE WAS NOT SAVED");
        }

        m.addAttribute("form", data);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());



        return "redirect:/a/message-home";
    }


}
