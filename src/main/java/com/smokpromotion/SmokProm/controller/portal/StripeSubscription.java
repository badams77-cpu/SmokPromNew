package com.smokpromotion.SmokProm.controller.portal;

import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import kotlin.random.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.UUID;

@Profile("smok_app")
@Controller
public class StripeSubscription extends PortalBaseController {

    @Value("${apiKey:null}")
    private String apiKey;

    private String priceId = "{{PRICE_ID}}";

    private HashMap<String, Integer> sessionIds;

    private HashMap<UUID, String> myUuidToStripeUuid;


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
        priceId = stripePriceId;
    }



    // The price ID passed from the client
//   String priceId = request.queryParams("priceId");
    String priceId = "{{PRICE_ID}}";

    @GetMapping("/a/bill")
    public String billing(Authentication auth) throws StripeException, NotLoggedInException,  UserNotFoundException {
        S_User user = getAuthUser(auth);
        UUID mySessionId = UUID.randomUUID();
        SessionCreateParams params = new SessionCreateParams.Builder()
                .setSuccessUrl("https://www.vapid-promotion.com/a/billing/"+user.getId()+"/"+mySessionId+"/activate")
                .setCancelUrl("https://www.vapid-promotion.com/a/billing/a/billing/"+user.getId()+"/"+mySessionId+"/deactivate")
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addLineItem(new SessionCreateParams.LineItem.Builder()
                        // For metered billing, do not pass quantity
                        .setQuantity(1L)
                        .setPrice(priceId)
                        .build()
                )
                .build();

        Session session = Session.create(params);
        sessionIds.put(session.getId(), user.getId());
        myUuidToStripeUuid.put(mySessionId, session.getId());

// Redirect to the URL returned on the Checkout Session.
// With Spark, you can redirect with:
//   response.redirect(session.getUrl(), 303);
//   return "";

        return "redirect:" + session.getUrl();
    }


    @GetMapping("/a/billing/{userId}/{sessionUd}/activate")
    public String setBillingActive(@PathVariable("userId") int userId, (@PathVariable("sessionId") int sessionId, Authentication auth)
            throws NotLoggedInException,  UserNotFoundException {
        S_User user = getAuthUser(auth);
        if (user.getId()==userId && sessionIds.get(myUuidToStripeUuid.get(sessionId)).equals(userId)){
            user.setPaying(true);
            userService.update(user);
        }
        return PRIBASE+"billing-success";
    }

    @GetMapping("/a/billing/{userId}/deactivate")
    public String setBillingCancelled(@PathVariable("userId") int userId,  (@PathVariable("sessionId")
    int sessionId,Authentication auth) throws NotLoggedInException,  UserNotFoundException {
        S_User user = getAuthUser(auth);
        if (user.getId()==userId && sessionIds.get(myUuidToStripeUuid.get(sessionId)).equals(userId)){
            user.setPaying(false);
            userService.update(user);
        }
        return PRIBASE+"billing-cancelled";
    }

}