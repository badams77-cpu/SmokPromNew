package com.smokpromotion.SmokProm.tweeter;

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

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Scanner;

public class CreateTweet {

    private static final String CODE = "FDCode101";

    TwitterApi apiInstance;

    DistillerTweeter main;


    int id;


    public CreateTweet(DistillerTweeter main){
        this.main = main;

        TwitterCredentialsOAuth2 cred =  new  TwitterCredentialsOAuth2(System.getenv("TWITTER_OAUTH2_CLIENT_ID"),
                System.getenv("TWITTER_OAUTH2_CLIENT_SECRET"),
                System.getenv("TWITTER_OAUTH2_ACCESS_TOKEN"),
                System.getenv("TWITTER_OAUTH2_REFRESH_TOKEN"));
          cred.setOAUth2AutoRefreshToken(true);
//                  apiInstance.setTwitterCredentials(new TwitterCredentialsBearer(System.getenv("TWITTER_BEARER_TOKEN")));
        apiInstance = new TwitterApi(cred);
        OAuth2AccessToken accessToken = getAccessToken(cred);
        if (accessToken == null) {
            return;
        }

        // Setting the access & refresh tokens into TwitterCredentialsOAuth2
        cred.setTwitterOauth2AccessToken(accessToken.getAccessToken());
        cred.setTwitterOauth2RefreshToken(accessToken.getRefreshToken());
         id = 0;
       }

    public OAuth2AccessToken getAccessToken(TwitterCredentialsOAuth2 credentials) {
        TwitterOAuth20Service service = new TwitterOAuth20Service(
                credentials.getTwitterOauth2ClientId(),
                credentials.getTwitterOAuth2ClientSecret(),
                "https://fd.feeddistiller.com/twitter.jsp",
                "offline.access tweet.read users.read tweet.write");
        File codeFile = new File("/tmp/twitter_code");
        OAuth2AccessToken accessToken = null;
        try {

            System.out.println("Fetching the Authorization URL...");

            final String secretState = "state";
            PKCE pkce = new PKCE();
            pkce.setCodeChallenge("challenge");
            pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
            pkce.setCodeVerifier("challenge");
            String authorizationUrl = service.getAuthorizationUrl(pkce, secretState);

            System.out.println("Go to the Authorization URL and authorize your App:\n" +
            authorizationUrl + "\nAfter that paste the authorization code here\n>>");
//            final String code = CODE;
                        System.out.println("\nTrading the Authorization Code for an Access Token...");
            while(!codeFile.exists()) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {

                }
            }
            String code = "";
            FileInputStream reader =new FileInputStream(codeFile);
            final Scanner in = new Scanner(reader, "UTF-8");
            try {
                code = in.nextLine();
            } catch (Exception e){}
            reader.close();
            System.out.println(code+"\n----");
            accessToken = service.getAccessToken(pkce, code);


            System.out.println("Access token: " + accessToken.getAccessToken());
            System.out.println("Refresh token: " + accessToken.getRefreshToken());
        } catch (Exception e) {
            System.err.println("Error while getting the access token:\n " + e);
            e.printStackTrace();
        }
        return accessToken;
    }

       public void tweet(String text){
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
