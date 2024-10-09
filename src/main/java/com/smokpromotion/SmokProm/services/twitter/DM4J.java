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
import twitter4j.AccessToken;
import twitter4j.OAuthAuthorization;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.DirectMessage;
import twitter4j.v1.StatusUpdate;

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
        for(Integer userId : usersWithCodes){
            try {
                sendTweetsAndDMs(userId);
            } catch (UserNotFoundException e){
                LOGGER.warn("UserId not found: "+userId);
            }
        }
    }

    public void sendTweetsAndDMs(int userId) throws UserNotFoundException {

        String consumerKey = "";
        String consumerSecret = "";
        OAuthAuthorization oAuth = OAuthAuthorization.newBuilder()
                .oAuthConsumer(consumerKey, consumerSecret).build();
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

        Optional<DE_AccessCode> accessCodeOptional = repAccessCode.getLastCodeForUser(userId);

        if (accessCodeOptional.isEmpty()) {
            return;
        }

        String access = accessCodeOptional.get().getAccessCode();
        String requestToken = accessCodeOptional.get().getRequestToken();

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
                return;
                //         System.out.println("Unable to get the access token.");
                //         return;
                //     }
            }
        }


        Twitter twitter = Twitter.newBuilder().oAuthConsumer(consumerKey, consumerSecret)
                .oAuthAccessToken(accessToken).build();
        //persist to the accessToken for future reference.
        //storeAccessToken(twitter.v1().users().verifyCredentials().getId(), accessToken);

        S_User user = userRepo.getById(userId);

        if (!user.isPaying()){
            return;
        }

        List<DE_SeduledTwitterSearch> sdt = repoSeduledTwitterSearch.getUserIdsLast7DaysUnsentWithCodes(userId);
        for (DE_SeduledTwitterSearch sds : sdt) {

            DE_TwitterSearch ts = null;

            try {

                repoTwitterSearch.getById(sds.getTwitterSearchId(), sds.getUserId());

            } catch (TwitterSearchNotFoundException e) {

            }
            // id sendDM(int userId, int searchId, int replyId) throws TwitterException {
            List<DE_SearchResult> todaysResults = resultRepo.findByUserUnsent(userId, sds.getId());


            long recipientId = 0; // get from ReplyId read from DB
            String message = "message"; // get from Twitter Search from DB
//        Twitter twitter = Twitter.getInstance();

            int sentCount = 0;
            boolean sent = false;

            if (GenericUtils.isValid(ts.getText())) {
                for (DE_SearchResult sr : todaysResults) {
                    long receip = sr.getTwitterUserId();
                    if (receip == 0) {
                        continue;
                    }
                    try {
                        DirectMessage directMessage = twitter.v1().directMessages().sendDirectMessage(receip, ts.getMessage());
                        sent = true;
                        sentCount++;
                        // Mark Sent DM

                    } catch (TwitterException e) {
                    }
                    //             System.out.printf("Sent: %s to @%d%n", directMessage.getText(), directMessage.getRecipientId());
                }
            }

            if (GenericUtils.isValid(ts.getMessage())) {
                for (DE_SearchResult sr : todaysResults) {
                    long twiNum = sr.getTweetId();
                    if (twiNum == 0) {
                        continue;
                    }
                    StatusUpdate stat = StatusUpdate.of(ts.getMessage());
                    stat.inReplyToStatusId(twiNum);

                    //             GeoLocation location = new GeoLocation(latitude, longitude);
                    //             stat.s(location);
                    try {
                        twitter.v1().tweets().updateStatus(stat);
                        sent = true;
                        sentCount++;
                    } catch (Exception e){

                    }

                    sds.setNsent(sentCount);
                    repoSeduledTwitterSearch.update(sds);

                    // Mark reply sent
                }
                //             System.out.printf("Sent: %s to @%d%n", directMessage.getText(), directMessage.getRecipientId());
            }
        }
    }
}
