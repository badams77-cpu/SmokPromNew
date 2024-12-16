package com.smokpromotion.SmokProm.controller.portal;

import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_TwitterSearch;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.stripe.Stripe;
import com.stripe.Stripe.*;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import kotlin.random.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Profile("smok_app")
@Controller
public class StripeSubscription extends PortalBaseController {



    private String apiKey;

    private String priceId = "price_1QCu4mFrTtrppWcAZx4BaPqh";

    private HashMap<String, Integer> sessionIds;


    private HashMap<UUID, String> myUuidToStripeUuid;

    private HashMap< String, UUID> stripeIdtoMyUuud;

    private HashMap<UUID, Integer> myUuidToStripePaidQuant;

    @Autowired
    private REP_TwitterSearch repTwitterSearch;

    public StripeSubscription(@Value("${stripKey:}") String stripKey){
        apiKey = "sk_live_51"+stripKey;
    }

    // Set your secret key. Remember to switch to your live secret key in production.
// See your keys here: https://dashboard.stripe.com/apikeys
    //  Stripe.apiKey = "sk_test_51NbwagFrTtrppWcAfH9HZNSq3NHggoB5UC7qPa4Nbl60ZvIshmvxtYyCWxSnDFSMhV99eO4xNik7mHTRJIIDAjHY00qQPQ0rJN"
    @Autowired
    public StripeSubscription(@Value("${stripeApiKey:null") String apiKey,
        @Value("{stripePriceId") String stripePriceId
    ) {
        Stripe.apiKey = apiKey;
        sessionIds = new HashMap<>();
        myUuidToStripeUuid = new HashMap<>();
 //       priceId = stripePriceId;
        myUuidToStripePaidQuant = new HashMap<>();
        stripeIdtoMyUuud = new HashMap<>();
    }

    @GetMapping("/a/billing")
    public String billing(Model m, Authentication auth) throws StripeException, NotLoggedInException,  UserNotFoundException {
        S_User user = getAuthUser(auth);
        m.addAttribute("paying", user.getSubCount());
        List<DE_TwitterSearch> searches = repTwitterSearch.findByUserIdActive(user.getId());
        int nsearch = searches.size();
        m.addAttribute("nsearches", nsearch);
        return PRIBASE+"billing";
    }


    @GetMapping("/a/billing-stripe")
    public String billing(Authentication auth) throws StripeException, NotLoggedInException,  UserNotFoundException {
        S_User user = getAuthUser(auth);
        UUID mySessionId = UUID.randomUUID();

        List<DE_TwitterSearch> searches = repTwitterSearch.findByUserIdActive(user.getId());
        int nsearch = searches.size();

        myUuidToStripePaidQuant.put(mySessionId, nsearch);

        RequestOptions ops = RequestOptions.builder()
                .setApiKey(apiKey)                 .build();

        Stripe.apiKey=  apiKey;

        SessionCreateParams params = new SessionCreateParams.Builder()
                .setSuccessUrl("https://www.vapid-promotion.com/a/billing/"+user.getId()+"/"+mySessionId+"/activate")
                .setCancelUrl("https://www.vapid-promotion.com/a/billing/a/billing/"+user.getId()+"/"+mySessionId+"/deactivate")
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addLineItem(new SessionCreateParams.LineItem.Builder()
                        // For metered billing, do not pass quantity
                        .setQuantity(0L+nsearch)
                        .setPrice(priceId)
                        .build()
                )
                .build();

        Session session = Session.create(params);
        sessionIds.put(session.getId(), user.getId());
        stripeIdtoMyUuud.put(session.getId(), mySessionId);
        myUuidToStripeUuid.put(mySessionId, session.getId());

// Redirect to the URL returned on the Checkout Session.
// With Spark, you can redirect with:
//   response.redirect(session.getUrl(), 303);
//   return "";

        return "redirect:" + session.getUrl();
    }


    @GetMapping("/a/billing/{userId}/{sessionUd}/activate")
    public String setBillingActive(@PathVariable("userId") int userId, @PathVariable("sessionId") int sessionId, Authentication auth)
            throws NotLoggedInException,  UserNotFoundException {
        S_User user = getAuthUser(auth);
        if (user.getId()==userId && sessionIds.get(stripeIdtoMyUuud.getOrDefault(sessionId, UUID.randomUUID())).equals(userId)){

            user.setSubCount(myUuidToStripePaidQuant.getOrDefault(
                    stripeIdtoMyUuud.getOrDefault(sessionId, UUID.randomUUID()), user.getSubCount()));
            userService.update(user);
        }
        return PRIBASE+"billing-success";
    }

    @GetMapping("/a/billing/{userId}/deactivate")
    public String setBillingCancelled(@PathVariable("userId") int userId,  @PathVariable("sessionId")
    int sessionId,Authentication auth) throws NotLoggedInException,  UserNotFoundException {
        S_User user = getAuthUser(auth);
        if (user.getId()==userId && sessionIds.get(myUuidToStripeUuid.get(sessionId)).equals(userId)){
            user.setSubCount(0);
            userService.update(user);
        }
        return PRIBASE+"billing-cancelled";
    }

}