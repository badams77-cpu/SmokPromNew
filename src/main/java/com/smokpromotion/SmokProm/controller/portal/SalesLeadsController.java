package com.smokpromotion.SmokProm.controller.portal;

import com.smokpromotion.SmokProm.domain.entity.LeadStatus;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.entity.SalesLeadEntity;
import com.smokpromotion.SmokProm.domain.entity.VPMessage;
import com.smokpromotion.SmokProm.domain.repo.REP_SalesLeadEntity;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.domain.repo.REP_VPMessage;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.LinkedList;
import java.util.List;

@Controller
public class SalesLeadsController extends PortalBaseController{

    private static Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(SearchController.class);


    private static final String adminEmail="vapidpromotions@gmail.com";


    @Autowired
    private REP_SalesLeadEntity salesRepo;

    @Autowired
    public SalesLeadsController(REP_UserService userService) {
    }

    @RequestMapping("/a/message-home")
    public String leadsHome(Model m, @RequestParam(name="period") int period, Authentication auth) throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);

        if (period==0){period = 10000; }

        LocalDate start = LocalDate.now().minusDays(period);

        List<SalesLeadEntity> leads = salesRepo.findByUserAfterDate(user.getId(),start);

        m.addAttribute("leads", leads);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());

        return PRIBASE+"sale_leads";
    }








    @RequestMapping(value = "/a/sales-leads-post", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE )
    public String searchAddPost(@RequestBody MultiValueMap<String, String> data, Model m, Authentication auth) throws Exception, UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);
        SalesLeadEntity mess = new SalesLeadEntity();
        mess.setLeadStatus(LeadStatus.NEW);
        List<String> def = new LinkedList<>();
        def.add("10000");

        List<String> defHandle = new LinkedList<>();
        def.add("Empty");

        int period = Integer.parseInt(data.getOrDefault("period",def).get(0).toString());
 //       try {
                   mess.setTwitterHandle(data.getOrDefault("twitter_handle",defHandle).get(0).toString());
  //      } catch (Exception e){
  //          LOGGER.warn("WARNING Search form binding ");
  //          return PRIBASE+"messages";
  //      }

     //   twitterSearchForm.setUserId(user.getId());



        int newId = salesRepo.create(mess);

        if (newId==0){
            LOGGER.warn("WARNING *** NEW SALES LEAD WAS NOT SAVED");
        }

        m.addAttribute("form", data);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());



        return "redirect:/a/message-home";
    }


}
