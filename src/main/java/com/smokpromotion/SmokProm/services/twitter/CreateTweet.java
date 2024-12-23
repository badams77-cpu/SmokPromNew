package com.smokpromotion.SmokProm.services.twitter;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import com.twitter.clientlib.model.Problem;
import com.twitter.clientlib.model.TweetCreateRequest;
import com.twitter.clientlib.model.TweetCreateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Scanner;

@Service
public class CreateTweet {


    private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(CreateTweet.class);

    private static final String CODE = "FDCode101";

    TwitterApi apiInstance;

    DistillerTweeter main;


    int id;

    private TwitterCredentialsOAuth2 credentials;

    @Autowired
    public CreateTweet(
            @Value("${twitoauth.clientId:}") String clientId,
            @Value("${twitoauth.clientSecret:}") String clientSecret,
            @Value("${twitter.accessToken:}") String accessToken,
            @Value("${twitter.accessSecret:}") String accessSecret
    ) {
        credentials = new TwitterCredentialsOAuth2(clientId, clientSecret,
                accessToken,
                accessToken);
        credentials.setOAUth2AutoRefreshToken(true);
        if (clientId.equals("") || clientId.equals("")) {
            LOGGER.error("createtweet No twitoauth credentials");
        } else {
            LOGGER.error("createtweet Have twitoauth credentials");
        }
//                  apiInstance.setTwitterCredentials(new TwitterCredentialsBearer(System.getenv("TWITTER_BEARER_TOKEN")));
        apiInstance = new TwitterApi(credentials);

    }

    public TwitterApi getCredentials(String code){
        OAuth2AccessToken accessToken = getAccessToken( code);
        if (accessToken == null) {
            return null;
        }

        // Setting the access & refresh tokens into TwitterCredentialsOAuth2
        credentials.setTwitterOauth2AccessToken(accessToken.getAccessToken());
        credentials.setTwitterOauth2RefreshToken(accessToken.getRefreshToken());
        // Add Oauth 2 user token
        apiInstance = new TwitterApi(credentials);

        return apiInstance;
    }

    public OAuth2AccessToken getAccessToken( String code) {
        TwitterOAuth20Service service = new TwitterOAuth20Service(
                credentials.getTwitterOauth2ClientId(),
                credentials.getTwitterOAuth2ClientSecret(),
                "https://www.vapidpromotions.com/tcallback",
                "offline.access dm.write tweet.read users.read tweet.write");
        File codeFile = new File("/tmp/twitter_code");
        OAuth2AccessToken accessToken = null;
        try {

            LOGGER.warn("Fetching the Authorization URL...");

            final String secretState = "state_code_0";
            PKCE pkce = new PKCE();
            pkce.setCodeChallenge("challenge");
            pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
            pkce.setCodeVerifier("challenge");
            String authorizationUrl = service.getAuthorizationUrl(pkce, secretState);
            return authorizationUrl;
        } catch (Exception e) {
            LOGGER.warn("Error while getting the auth url:\n " ,e);
        }
        return "";
    }


    public OAuth2AccessToken getAccessToken( String code) {
        TwitterOAuth20Service service = new TwitterOAuth20Service(
                credentials.getTwitterOauth2ClientId(),
                credentials.getTwitterOAuth2ClientSecret(),
                "https://www.vapidpromotions.com/tcallback",
                "offline.access dm.write tweet.read users.read tweet.write");
        File codeFile = new File("/tmp/twitter_code");
        OAuth2AccessToken accessToken = null;
        try {

            LOGGER.warn("Fetching the Authorization URL...");

            final String secretState = "state";
            PKCE pkce = new PKCE();
            pkce.setCodeChallenge("challenge");
            pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
            pkce.setCodeVerifier("challenge");
            String authorizationUrl = service.getAuthorizationUrl(pkce, secretState);

            accessToken = service.getAccessToken(pkce, code);

            LOGGER.warn("Access token: " + accessToken.getAccessToken());
            LOGGER.warn("Refresh token: " + accessToken.getRefreshToken());
        } catch (Exception e) {
            LOGGER.warn("Error while getting the access token:\n " ,e);
        }
        return accessToken;
    }

       public void tweet(TwitterApi apiInstance, String text){
           TweetCreateRequest req = new TweetCreateRequest();
           req.setText(text);
 //          req.setDirectMessageDeepLink("");
           req.setQuoteTweetId(""+(new Date()).getTime());
           id++;
           req.setReplySettings(TweetCreateRequest.ReplySettingsEnum.FOLLOWING);
           apiInstance.getApiClient();
           TweetsApi tapi = new TweetsApi();
           tapi.setClient(apiInstance.getApiClient());
           TweetCreateResponse res = null;
           try {
               res = tapi.createTweet(req).execute();
               if (res.getErrors()!=null) {
                   for (Problem prob : res.getErrors()) {
                       main.log(prob.toJson());
                   }
               }
               main.log(res.toJson());
           } catch (ApiException e){
               main.log(e+"");
               main.log(e.getMessage());
               main.logEx(e);
               main.logEx(e.getCause());
               main.log(e.getResponseBody());
           }
           return;
       }


}
