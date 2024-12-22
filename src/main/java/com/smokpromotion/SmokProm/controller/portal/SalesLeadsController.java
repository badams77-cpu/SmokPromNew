package com.smokpromotion.SmokProm.controller.portal;

import com.smokpromotion.SmokProm.domain.entity.*;
import com.smokpromotion.SmokProm.domain.repo.REP_SalesLeadEntity;
import com.smokpromotion.SmokProm.domain.repo.REP_SalesLeadNote;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.domain.repo.REP_VPMessage;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.services.ai.OpenAIService;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
@Profile("smok_app")
@Controller
public class SalesLeadsController extends PortalBaseController{

    private static Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(SearchController.class);


    private static final String adminEmail="vapidpromotions@gmail.com";

    @Autowired
    private OpenAIService aiService;

    @Autowired
    private REP_SalesLeadEntity salesRepo;

    @Autowired
    private REP_SalesLeadNote notesRepo;

    @Autowired
    public SalesLeadsController(REP_UserService userService) {
    }

    @RequestMapping("/a/sales-leads")
    public String leadsHome(Model m, @RequestParam(name="period", required = false) Integer periodInt, Authentication auth) throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);

        int period = periodInt==null ? 0 : periodInt;

        if (period==0){period = 10000; }

        LocalDate start = LocalDate.now().minusDays(period);

        List<SalesLeadEntity> leads = salesRepo.findByUserAfterDate(user.getId(),start);

        m.addAttribute("leads", leads);

        m.addAttribute("period",period);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());

        return PRIBASE+"sales_leads";
    }

    @RequestMapping("/a/sales-notes/{id}")
    public String notesHome(Model m, @PathVariable(name="id") int id, Authentication auth) throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);

        Optional<SalesLeadEntity> ent = salesRepo.getById(user.getId(), id);

        if (!ent.isPresent()){
            return "redirect:/a/sales_leads";
        }

        List<SalesLeadNote> notes = notesRepo.findByUserAndEntityId(user.getId(), id);

        m.addAttribute("notes", notes);

        m.addAttribute("lead", ent.get());

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());

        return PRIBASE+"sales_notes";
    }



    @RequestMapping(value="/a/sales-lead-cycle/{id}")
    public String leadsCycle(Model m, @RequestParam(name="period") int period, @PathVariable("id") int id, Authentication auth) throws UserNotFoundException, NotLoggedInException {
        S_User user = getAuthUser(auth);
        Optional<SalesLeadEntity> ent = salesRepo.getById(user.getId(), id);
        if (ent.isPresent()){
            SalesLeadEntity ent1 = ent.get();
            LeadStatus ls = ent.get().getLeadStatus();
            int i =  ls.ordinal();

            List<LeadStatus>  stats =
                    new LinkedList<>();
            stats.addAll(Arrays.asList(LeadStatus.values()));
            stats.addAll(Arrays.asList(LeadStatus.values()));
            ls = stats.get(i+1);
            ent1.setLeadStatus(ls);
            ent1.setUserId(user.getId());
            salesRepo.update(ent1);
        }
        return "redirect:/a/sales-leads?period="+period;
    }

    @RequestMapping("a/sales-prompt")
    public String sellPromptPage(Model m){
        return PRIBASE+"sales_prompt";
    }

    @RequestMapping(value="/a/sales-prompt-post")
    public String sellPrompt(@RequestBody MultiValueMap<String, String> data, Model m, Authentication auth) throws Exception, UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);
        List<String> def = new LinkedList<>();
        def.add("10000");

        List<String> defHandle = new LinkedList<>();
        def.add("Empty");

        List<String> what = data.getOrDefault("what", new LinkedList<>());

        List<String> userWants = data.getOrDefault("user-wants", new LinkedList<>());

        if (what.size()>0 && userWants.size()>0) {
            String prompt = "Write two paragraphs to sell "+what.get(0)+" to a customers that wants "+userWants.get(0);
            m.addAttribute("response", aiService.chat(prompt));
        }
        m.addAttribute("form", data);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());



        return PRIBASE+"sales_prompt";
    }



    @RequestMapping(value = "/a/sales-leads-post", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE )
    public String leadAddPost(@RequestBody MultiValueMap<String, String> data, Model m, Authentication auth) throws Exception, UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);
        SalesLeadEntity mess = new SalesLeadEntity();
        mess.setLeadStatus(LeadStatus.NEW);
        mess.setUserId(user.getId());
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



        return "redirect:/a/sales-leads?period="+period;
    }

    @RequestMapping(value = "/a/sales-notes-post/{id}", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE )
    public String noteAddPost(
            @PathVariable(name="id") int id,
            @RequestBody MultiValueMap<String, String> data, Model m, Authentication auth) throws Exception, UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);
        SalesLeadNote mess = new SalesLeadNote();
        mess.setLeadEntityId(id);
        mess.setUserId(user.getId());
        List<String> def = new LinkedList<>();
        def.add("10000");

        List<String> defHandle = new LinkedList<>();
        def.add("Empty");

        //       try {
        mess.setNoteType(
                NoteType.fromString(
                    data.getOrDefault("note_type",defHandle).get(0).toString()));
        mess.setText(
                        data.getOrDefault("note_text",defHandle).get(0).toString());
        //      } catch (Exception e){
        //          LOGGER.warn("WARNING Search form binding ");
        //          return PRIBASE+"messages";
        //      }

        //   twitterSearchForm.setUserId(user.getId());



        int newId = notesRepo.create(mess);

        if (newId==0){
            LOGGER.warn("WARNING *** NEW SALES LEAD NOTE WAS NOT SAVED");
        }

        m.addAttribute("form", data);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());



        return "redirect:/a/sales-notes/"+id;
    }


}
