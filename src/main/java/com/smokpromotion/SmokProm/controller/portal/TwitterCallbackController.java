package com.smokpromotion.SmokProm.controller.portal;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.smokpromotion.SmokProm.domain.entity.DE_AccessCode;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.entity.VPMessage;
import com.smokpromotion.SmokProm.domain.repo.REP_AccessCode;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.domain.repo.REP_VPMessage;
import java.io.IOException;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.services.twitter.CreateTweet;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Profile("smok_app")
@Controller
public class TwitterCallbackController extends PortalBaseController{

    private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(TwitterCallbackController.class);

    private final CreateTweet createTweet;

    private final REP_AccessCode accessRepo;

    @Autowired
    public TwitterCallbackController(REP_AccessCode accessRepo, CreateTweet createTweet) {
        this.accessRepo = accessRepo;
        this.createTweet = createTweet;
    }

    @RequestMapping("/tcallback")
    public String callback(Model m,
                              @RequestParam("state") String state,
                              @RequestParam("code") String code,
                              Authentication auth
                              )   throws UserNotFoundException, NotLoggedInException
    {
        S_User user = getAuthUser(auth);
        Optional<DE_AccessCode> accessCodeOpt = accessRepo.getLastCodeWithoutAccessForUser(user.getId());

        if (accessCodeOpt.isEmpty()
                        || !state.equalsIgnoreCase("state_code_0")
        ) {
            LOGGER.warn("Access code present"+accessCodeOpt.isPresent());
            LOGGER.warn("State was "+state);
            return PRIBASE+"callback_failed";
        }

        DE_AccessCode code1 = accessCodeOpt.get();

        code1.setAccessCode(code);

        try {

            OAuth2AccessToken tok = createTweet.getAccessToken(code, state);

            if (tok == null) {
                return PRIBASE + "callback_failed_tok";
            }

            code1.setAccessCode(tok.getAccessToken());

            accessRepo.update(code1);

        } catch (Exception e){
           LOGGER.warn("State was "+state);
           return PRIBASE+"callback_failed_tok";
        }


        return PRIBASE+"callback_success";
    }

}
