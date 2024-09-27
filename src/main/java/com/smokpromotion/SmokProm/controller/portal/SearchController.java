package com.smokpromotion.SmokProm.controller.portal;

import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_TwitterSearch;
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

import java.util.List;

@Controller
public class SearchController extends PortalBaseController{

    private static Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(SearchController.class);

    @Autowired
    private REP_TwitterSearch searchRepo;


    @RequestMapping("/a/search-home")
    public String searchHome(Model m, Authentication auth) throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);

        List<DE_TwitterSearch> searches = searchRepo.findByUserId(user.getId());

        m.addAttribute("searches", searches);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());

        return PRIBASE+"searches";
    }



    @RequestMapping("/a/search-add")
    public String searchAdd(Model m, Authentication auth) throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);

        List<DE_TwitterSearch> searches = searchRepo.findByUserId(user.getId());


        m.addAttribute("form", new DE_TwitterSearch());

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());

        return PRIBASE+"search-form-add";
    }

    @RequestMapping("/a/search-edit/{id}")
    public String searchEdit(Model m, Authentication auth, @PathVariable("id") int id) throws TwitterSearchNotFoundException, UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);

         m.addAttribute("edit_id", id);

        DE_TwitterSearch search = searchRepo.getById(id, user.getId());

        m.addAttribute("form", search);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());

        return PRIBASE+"search-form-edit";
    }

    @RequestMapping("/a/search-add-post")
    public String searchAddPost(@Valid DE_TwitterSearch twitterSearchForm, BindingResult bindingResult, Model m, Authentication auth) throws TwitterSearchNotFoundException, UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);

        if (bindingResult.hasErrors()) {
            LOGGER.warn("WARNING Search form binding ",bindingResult.getFieldErrors());
            return "search-form-add";
        }

        twitterSearchForm.setUserId(user.getId());

        int newId = searchRepo.create(twitterSearchForm);

        if (newId==0){
            LOGGER.warn("WARNING *** NEW SEARCH WAS NOT SAVED");
        }

        m.addAttribute("form", twitterSearchForm);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());

        return "redirect:/a/search-home";
    }

    @RequestMapping("/a/search-edit-post/{id}")
    public String searchEditPost(@Valid DE_TwitterSearch twitterSearchForm, BindingResult bindingResult, Model m, Authentication auth, @PathVariable("id") int id) throws TwitterSearchNotFoundException, UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);


        m.addAttribute("edit_id", id);

        m.addAttribute("form", twitterSearchForm);

        m.addAttribute("userName", user.getFirstname()+" "+user.getLastname());

        if (bindingResult.hasErrors()) {
            return "search-form-edit";
        }


        twitterSearchForm.setId(id);
        twitterSearchForm.setUserId(user.getId());

        boolean changed = searchRepo.update(twitterSearchForm);


        return "redirect:/a/search-home";
    }


}
