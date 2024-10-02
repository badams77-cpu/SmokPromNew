package com.smokpromotion.SmokProm.services.twitter;

import com.smokpromotion.SmokProm.domain.entity.DE_SearchResult;
import com.smokpromotion.SmokProm.domain.entity.DE_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.DE_TodaysAccessCode;
import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.domain.repo.REP_SearchResult;
import com.smokpromotion.SmokProm.domain.repo.REP_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.repo.REP_TwitterSearch;
import com.smokpromotion.SmokProm.util.GenericUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import twitter4j.*;
import twitter4j.v1.DirectMessage;
import twitter4j.v1.GeoLocation;
import twitter4j.v1.Status;
import twitter4j.v1.StatusUpdate;

import java.util.List;

import static org.apache.poi.sl.draw.geom.Guide.Op.pin;

public class DM4J {

    @Value("${twitter_consumerKey:}")
    private String consumerKey;

    @Value("${twitter_consumerSecret:}")
    private String consumerSecret;

    @Autowired
    private REP_todaysAccessCode repTodaysAccessCode;

    @Autowired
    private REP_SearchResult resultRepo;


    @Autowired
    private REP_TwitterSearch repoTwitterSearch;

    @Autowired
    private REP_SeduledTwitterSearch repoSeduledTwitterSearch;

    private static void storeAccessToken(long userId, AccessToken accessToken) {
        //store accessToken.getToken()
        //store accessToken.getTokenSecret()
    }

    public void Oauth(int userId) throws TwitterException {

    //    String consumerKey = "";
    //    String consumerSecret = "";
        OAuthAuthorization oAuth = OAuthAuthorization.newBuilder()
                .oAuthConsumer(consumerKey, consumerSecret).build();
        RequestToken requestToken = oAuth.getOAuthRequestToken();
        AccessToken accessToken = null;
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



        DE_TodaysAccessCode accessCodeEntity = repTodaysAccessCode.getLast24CodeForUser();

        String access = accessCodeEntity.getAuth_code();

        if (pin.length() > 0) {
            accessToken = oAuth.getOAuthAccessToken(requestToken, pin);
        } else {
           String  accessToken = oAuth.getOAuthAccessToken();
        }
        if (401 == te.getStatusCode()) {
            System.out.println("Unable to get the access token.");
            return;
        }



        Twitter twitter = Twitter.newBuilder().oAuthConsumer(consumerKey, consumerSecret)
                .oAuthAccessToken(accessToken).build();
        //persist to the accessToken for future reference.
        //storeAccessToken(twitter.v1().users().verifyCredentials().getId(), accessToken);

        List<DE_SeduledTwitterSearch> sdt = repoSeduledTwitterSearch.findByUserIdLast7DaysUnsnet(userId);
        for(DE_SeduledTwitterSearch sds : sdt) {

            DE_TwitterSearch ts  = repoTwitterSearch.getById(sds.getTwitterSearchId(), sds.getUserId());

            // id sendDM(int userId, int searchId, int replyId) throws TwitterException {
            List<DE_SearchResult> todaysResults = resultRepo.getUnsendResultsInLast7days(userId);


            long recipientId = 0; // get from ReplyId read from DB
            String message = "message"; // get from Twitter Search from DB
//        Twitter twitter = Twitter.getInstance();

            int sentCount = 0;
            boolean sent=false;

            if (GenericUtils.isValid(ts.getText())) {
                for (DE_SearchResult sr : todaysResults) {
                    long receip = sr.getTwitterUserId();
                    if (receip==0){continue;}
                    try {
                        DirectMessage directMessage = twitter.v1().directMessages().sendDirectMessage(receip, ts.getMessage());
                        sent = true;
                        sentCount++;
                        // Mark Sent DM
                    } catch (Exception e) {
                    }
                    //             System.out.printf("Sent: %s to @%d%n", directMessage.getText(), directMessage.getRecipientId());
                }
            }

            if (GenericUtils.isValid(ts.getMessage())) {
            for (DE_SearchResult sr : todaysResults) {
                long twiNum = sr.getTweetId();
                if (twiNum==0){ continue; }
                StatusUpdate stat = StatusUpdate.of(ts.getMessage());
                stat.inReplyToStatusId(twiNum);

   //             GeoLocation location = new GeoLocation(latitude, longitude);
   //             stat.s(location);

                twitter.v1().tweets().updateStatus(stat);

                // Mark reply sent
            }
                //             System.out.printf("Sent: %s to @%d%n", directMessage.getText(), directMessage.getRecipientId());
        }
}
