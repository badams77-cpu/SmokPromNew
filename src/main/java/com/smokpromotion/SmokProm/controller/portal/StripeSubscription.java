package com.smokpromotion.SmokProm.controller.portal;

import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_TwitterSearch;
import com.smokpromotion.SmokProm.exceptions.NotLoggedInException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import com.stripe.Stripe;
import com.stripe.Stripe.*;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerListParams;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.checkout.SessionCreateParams;
import kotlin.random.Random;
import org.slf4j.Logger;
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


    private static Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(StripeSubscription.class);


    private String apiKey;

    private String priceId = "price_1QCu4mFrTtrppWcAZx4BaPqh";

    private HashMap<String, Integer> sessionIds;


    private HashMap<UUID, String> myUuidToStripeUuid;

    private HashMap< String, UUID> stripeIdtoMyUuud;

    private HashMap<UUID, Integer> myUuidToStripePaidQuant;

    @Autowired
    private REP_TwitterSearch repTwitterSearch;


//    public StripeSubscription(@Value("${stripKey:}") String stripKey){
//        apiKey = "sk_live_51"+stripKey;
//    }

    // Set your secret key. Remember to switch to your live secret key in production.
// See your keys here: https://dashboard.stripe.com/apikeys
    //  Stripe.apiKey = "sk_test_51NbwagFrTtrppWcAfH9HZNSq3NHggoB5UC7qPa4Nbl60ZvIshmvxtYyCWxSnDFSMhV99eO4xNik7mHTRJIIDAjHY00qQPQ0rJN"
    @Autowired
    public StripeSubscription(@Value("${stripKey:null}") String apiKey,
        @Value("{stripePriceId") String stripePriceId
    ) {
        this.apiKey = "sk_live_51"+apiKey;
        Stripe.apiKey = this.apiKey;
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

        int subCount = 0;

        try {

            CustomerListParams params = CustomerListParams.builder()
                    .setEmail(user.getUsername())
                    .setLimit(100L).build();
            CustomerCollection customers = Customer.list(params);
            for (Customer cust : customers.getData()) {
                SubscriptionListParams params1 =
                        SubscriptionListParams.builder().setLimit(100L).setCustomer(cust.getId()).build();
                SubscriptionCollection subscriptions = Subscription.list(params1);
                for (Subscription sub : subscriptions.getData()) {
                        for(SubscriptionItem si : sub.getItems().getData()) {
                            subCount+=si.getQuantity();
                        }
                }
            }

            LOGGER.warn(user.getUsername() + " Found: " + subCount + " subscriptions");

            userService.update(user);
        } catch (StripeException e){
            return PRIBASE+"cancel-failed-stripe";
        }


        SessionCreateParams params = new SessionCreateParams.Builder()
                .setSuccessUrl("https://www.vapidpromotions.com/a/billing/"+user.getId()+"/"+mySessionId+"/activate")
                .setCancelUrl("https://www.vapidpromotions.com/a/billing-failed")
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addLineItem(new SessionCreateParams.LineItem.Builder()
                        // For metered billing, do not pass quantity
                        .setQuantity(0L+nsearch-subCount)
                        .setPrice(priceId)
                        .build()
                )
                .build();

        Session session = Session.create(params);
        sessionIds.put(mySessionId.toString(), user.getId());
        stripeIdtoMyUuud.put(session.getId(), mySessionId);
        myUuidToStripeUuid.put(mySessionId, session.getId());



// Redirect to the URL returned on the Checkout Session.
// With Spark, you can redirect with:
//   response.redirect(session.getUrl(), 303);
//   return "";

        return "redirect:" + session.getUrl();
    }

    @GetMapping("/a/cancel-stripe")
    public String cancel(Authentication auth) throws StripeException, NotLoggedInException,  UserNotFoundException {
        S_User user = getAuthUser(auth);
        UUID mySessionId = UUID.randomUUID();

        List<DE_TwitterSearch> searches = repTwitterSearch.findByUserIdActive(user.getId());
        int nsearch = searches.size();

        myUuidToStripePaidQuant.put(mySessionId, nsearch);

        RequestOptions ops = RequestOptions.builder()
                .setApiKey(apiKey)                 .build();

        Stripe.apiKey=  apiKey;


        try {
            int subCount = 0;
            CustomerListParams params = CustomerListParams.builder()
                    .setEmail(user.getUsername())
                    .setLimit(100L).build();
            CustomerCollection customers = Customer.list(params);
            for (Customer cust : customers.getData()) {
                SubscriptionListParams params1 =
                        SubscriptionListParams.builder().setLimit(100L).setCustomer(cust.getId()).build();
                SubscriptionCollection subscriptions = Subscription.list(params1);
                for (Subscription sub : subscriptions.getData()) {
                    sub.cancel();
                }
                subCount++;

            }
            LOGGER.warn(user.getUsername() + " Cancelled: " + subCount + " subscriptions");

            user.setSubCount(0);
            userService.update(user);
        } catch (StripeException e){
            return PRIBASE+"cancel-failed-stripe";
        }
       /*
        SessionCreateParams params = new SessionCreateParams.Builder()
                .setSuccessUrl("https://www.vapidpromotions.com/a/billing/"+user.getId()+"/"+mySessionId+"/deactivate")
                .setCancelUrl("https://www.vapidpromotions.com/a/cancel-failed")
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addLineItem(new SessionCreateParams.LineItem.Builder()
                        // For metered billing, do not pass quantity
                        .setQuantity(0L)
                        .setPrice(priceId)
                        .build()
                )
                .build();

        Session session = Session.create(params);
        sessionIds.put(mySessionId.toString(), user.getId());
        stripeIdtoMyUuud.put(session.getId(), mySessionId);
        myUuidToStripeUuid.put(mySessionId, session.getId());

// Redirect to the URL returned on the Checkout Session.
// With Spark, you can redirect with:
//   response.redirect(session.getUrl(), 303);
//   return "";
*/
        return "redirect:/a/billing";
    }

    @GetMapping("/a/billing/{userId}/{sessionId}/activate")
    public String setBillingActive(@PathVariable("userId") int userId, @PathVariable("sessionId") String sessionId, Authentication auth)
            throws NotLoggedInException,  UserNotFoundException {
        S_User user = getAuthUser(auth);
        if (user.getId()==userId){
            UUID sessionUUID = UUID.fromString(sessionId);
            Integer sessUserId = sessionIds.getOrDefault(sessionId, 0);
            if( sessUserId!=null && sessUserId.intValue()==userId) {
                user.setSubCount( user.getSubCount()+myUuidToStripePaidQuant.getOrDefault(
                        sessionUUID, 0));
                userService.update(user);
                /*
                try {
                    int subCount = 0;
                    CustomerListParams params = CustomerListParams.builder()
                            .setEmail(user.getUsername())
                            .setLimit(100L).build();
                    CustomerCollection customers = Customer.list(params);
                    for (Customer cust : customers.getData()) {
                        SubscriptionListParams params1 =
                                SubscriptionListParams.builder().setLimit(100L).setCustomer(cust.getId()).build();
                        SubscriptionCollection subscriptions = Subscription.list(params1);
                        for(Subscription sub : subscriptions.getData()) {
                            for(SubscriptionItem si : sub.getItems().getData()) {
                                subCount+=si.getQuantity();
                            }
                        }
                        user.setSubCount(subCount);
                    }
                } catch (StripeException e){
                    return PRIBASE+"billing-failed-stripe";
                }
                */
            } else {
                return PRIBASE+"billing-failed";
            }
        } else {
            return PRIBASE+"billing-failed";
        }
        return PRIBASE+"billing-success";
    }

    @GetMapping("/a/billing/{userId}/deactivate")
    public String setBillingCancelled(@PathVariable("userId") int userId,
    String sessionId,Authentication auth) throws NotLoggedInException,  UserNotFoundException {
        S_User user = getAuthUser(auth);

        try {
            int subCount = 0;
            CustomerListParams params = CustomerListParams.builder()
                    .setEmail(user.getUsername())
                    .setLimit(100L).build();
            CustomerCollection customers = Customer.list(params);
            for (Customer cust : customers.getData()) {
                SubscriptionListParams params1 =
                        SubscriptionListParams.builder().setLimit(100L).setCustomer(cust.getId()).build();
                SubscriptionCollection subscriptions = Subscription.list(params1);
                for(Subscription sub : subscriptions.getData()) {
                    if (sub.getLivemode()){
                        sub.cancel();
                    }
                }

            }
        } catch (StripeException e){
            return PRIBASE+"cancel-failed-customer";
        }

        if (user.getId()==userId){
            Integer sessUserId = sessionIds.get(stripeIdtoMyUuud.getOrDefault(sessionId, UUID.randomUUID()));
            if( sessUserId!=null && sessUserId.intValue()==userId) {
                user.setSubCount(0);
                userService.update(user);
            } else {
                return PRIBASE+"cancel-failed";
            }
        } else {
            return PRIBASE+"cancel-failed";
        }
        return PRIBASE+"billing-cancelled";
    }

    @GetMapping("/a/cancel-failed")
    public String cancelfailed(
    String sessionId,Authentication auth) throws NotLoggedInException,  UserNotFoundException {
            return PRIBASE+"cancel-failed";
    }

    @GetMapping("/a/billing-failed")
    public String bailingfailed(
            String sessionId,Authentication auth) throws NotLoggedInException,  UserNotFoundException {
        return PRIBASE+"billing-failed";
    }

}