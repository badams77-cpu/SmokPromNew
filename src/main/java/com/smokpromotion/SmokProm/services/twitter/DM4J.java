package com.smokpromotion.SmokProm.services.twitter;

import com.smokpromotion.SmokProm.domain.entity.*;
import com.smokpromotion.SmokProm.domain.repo.*;
import com.smokpromotion.SmokProm.exceptions.TwitterSearchNotFoundException;
import com.smokpromotion.SmokProm.exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.util.GenericUtils;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//import static org.apache.poi.sl.draw.geom.Guide.Op.pin;

@Service
public class DM4J {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(DM4J.class);

    @Value("${twitter_consumerKey:}")
    private String consumerKey;

    @Value("${twitter_consumerSecret:}")
    private String consumerSecret;

    @Autowired
    private REP_AccessCode repAccessCode;

    @Autowired
    private REP_SearchResult resultRepo;


    @Autowired
    private REP_UserService userRepo;


    @Autowired
    private REP_TwitterSearch repoTwitterSearch;

    @Autowired
    private REP_SeduledTwitterSearch repoSeduledTwitterSearch;

//    private static void storeAccessToken(long userId, AccessToken accessToken) {
    //store accessToken.getToken()
    //store accessToken.getTokenSecret()
    //   }


    @Scheduled(cron="0 0,10,20,30,40,50 * * * *")
    public void seduleGetUsersWithAccess(){
        List<Integer> usersWithCodes = repAccessCode.getUsersWithNewCodes();
        LOGGER.warn(usersWithCodes.size()+" users with access code "+usersWithCodes);
        for(Integer userId : usersWithCodes){
            LOGGER.warn("Tweets for userId "+userId);
            try {
                int i = sendTweetsAndDMs(userId);
                repAccessCode.updateSetCodeUsed(userId);
            } catch (UserNotFoundException e){
                LOGGER.warn("UserId not found: "+userId);
            }
        }
    }

    public int sendTweetsAndDMs(int userId) throws UserNotFoundException {

        int nsent = 0;

        String consumerKey = "";
        String consumerSecret = "";
           //     .oAuthConsumer(consumerKey, consumerSecret).build();


        //    RequestToken requestToken = oAuth.getOAuthRequestToken();
        //    AccessToken accessToken = null;
     /*   try (Scanner scanner = new Scanner(System.in)) {
            while (null == accessToken) {
                System.out.println("Open the following URL and grant access to your account:");
                System.out.println(requestToken.getAuthorizationURL());
                System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
                String pin = scanner.nextLine();
                try {
                    if (pin.length() > 0) {
                        accessToken = oAuth.getOAuthAccessToken(requestToken, pin);
                    } else {
                        accessToken = oAuth.getOAuthAccessToken();
                    }
                } catch (TwitterException te) {
                    if (401 == te.getStatusCode()) {
                        System.out.println("Unable to get the access token.");
                    } else {
                        te.printStackTrace();
                    }
                }
            }
        }*/

        Optional<DE_AccessCode> accessCodeOptional = repAccessCode.getLastCodeWithAccessForUser(userId);

        if (accessCodeOptional.isEmpty()) {
            LOGGER.warn("no acccess code for userid+"+userId);
            return nsent;
        }
        LOGGER.warn("found access code for userid+"+userId);

        String access = accessCodeOptional.get().getAccessCode();
        String requestToken = accessCodeOptional.get().getRequestToken();

        var conf = new ConfigurationBuilder()
//                .setOAuthAccessToken(access)
                .setOAuth2AccessToken(access)
                .setJSONStoreEnabled(true)
                .build();
        OAuthAuthorization oAuth = new OAuthAuthorization(conf);

        AccessToken accessToken = null;

        try {
            // ? PIN?
            //    if (pin.length() > 0) {
            //        accessToken = oAuth.getOAuthAccessToken(requestToken, pin);
            //    } else {

            accessToken = oAuth.getOAuthAccessToken();
            //    }
        } catch (TwitterException te) {
            if (401 == te.getStatusCode()) {
                LOGGER.warn("OAuth access token expired");
                return nsent;
                //         System.out.println("Unable to get the access token.");
                //         return;
                //     }
            }
        }


        Twitter twitter1 = new TwitterFactory(conf).getInstance();

//        Twitter twitter1 = new TwitterFactory().getInstance();
        twitter4j.TwitterV2 twitterV2 = TwitterV2ExKt.getV2(twitter1);

        //persist to the accessToken for future reference.
        //storeAccessToken(twitter.v1().users().verifyCredentials().getId(), accessToken);

        S_User user = userRepo.getById(userId);

        if (user.getSubCount()==0){
            // Send subscritpion reminder (but no code found there
            return nsent;
        }

        List<DE_SeduledTwitterSearch> sdt =
                repoSeduledTwitterSearch.getUserSearchesLast7DaysUnsentWithCodes(userId);

        int nSearch = user.getSubCount();

        int nSds = 0;
        for (DE_SeduledTwitterSearch sds : sdt) {

            DE_TwitterSearch ts = null;

            try {

                ts = repoTwitterSearch.getById(sds.getTwitterSearchId(), sds.getUserId());

            } catch (TwitterSearchNotFoundException e) {
                LOGGER.warn("User  "+user.getUsername()+" exception find twitter search",e);
                continue;
            }
            // id sendDM(int userId, int searchId, int replyId) throws TwitterException {
            List<DE_SearchResult> todaysResults = resultRepo.findByUserUnsent(userId, sds.getId());

            LOGGER.warn("user "+user.getUsername()+" scheduled results "+
                    sds.getId()+" has "+todaysResults.size()+" to send");


            if (!(nSds++ < nSearch)){
                LOGGER.warn("Out of Search Subscription");
                continue; // Seach limit reached
            }

            long recipientId = 0; // get from ReplyId read from DB
            String message = "message"; // get from Twitter Search from DB
//        Twitter twitter = Twitter.getInstance();

            int sentCount = 0;
            boolean sent = false;
/*
            if (GenericUtils.isValid(ts.getText())) {
                for (DE_SearchResult sr : todaysResults) {

                    try {
                        long receip = sr.getTwitterUserId();
                        if (receip == 0) {
                            continue;
                        }
                    //    DirectMessage directMessage =

                                 twitter1.directMessages().sendDirectMessage(receip, ts.getMessage());
                        sent = true;
                        sentCount++;
                        // Mark Sent DM

                    } catch (TwitterException e) {
                        LOGGER.warn("User  "+user.getUsername()+" exception with dm ",e);
                    }
                    //             System.out.printf("Sent: %s to @%d%n", directMessage.getText(), directMessage.getRecipientId());
                }
            }
*/
            if (GenericUtils.isValid(ts.getMessage())) {
                for (DE_SearchResult sr : todaysResults) {
                    long twiNum = sr.getTweetId();
                    if (twiNum == 0) {
                        continue;
                    }
//                    StatusUpdate stat = new StatusUpdate(ts.getMessage());
//                    stat.inReplyToStatusId(twiNum);


                    //             GeoLocation location = new GeoLocation(latitude, longitude);
                    //             stat.s(location);
                    try {
//                        twitter.tweets().updateStatus(stat);
                        twitterV2.createTweet(null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                                twiNum,
                        null,
                        ts.getMessage());
                        sent = true;
                        sentCount++;
                        sr.setSent(true);
                        resultRepo.update(sr);
                        nsent++;
                    } catch (Exception e){
                        LOGGER.warn("User  "+user.getUsername()+" exception with tweeting ",e);
                    }

                    sds.setNsent(sentCount);
                    repoSeduledTwitterSearch.update(sds);

                    // Mark reply sent
                }
                //             System.out.printf("Sent: %s to @%d%n", directMessage.getText(), directMessage.getRecipientId());
            }
        }
        return nsent;
    }
}
