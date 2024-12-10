package com.smokpromotion.SmokProm.services.twitter;

import com.smokpromotion.SmokProm.domain.entity.DE_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.DE_SearchResult;
import com.smokpromotion.SmokProm.domain.repo.REP_SearchResult;
import com.smokpromotion.SmokProm.domain.repo.REP_SeduledTwitterSearch;
import com.smokpromotion.SmokProm.domain.repo.REP_TwitterSearch;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.*;

import java.time.LocalDate;
import twitter4j.conf.ConfigurationBuilder;

@Service
public class Search4J {



    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(Search4J.class);

    // This is set to that the 100 Users Searches each with 15 texts a day, uses tweeter BASIC limit of 50,000 posts writes per month.
    private static final int SEARCH_COUNT = 15;

    @Autowired
    private REP_TwitterSearch searchRep;


    @Autowired
    private REP_SeduledTwitterSearch searchTryRep;


    @Autowired
    private REP_SearchResult resultsRep;

    private TwitterV2 twitter;


    @Autowired
    public Search4J(@Value("${twitter.consumerKey:}") String conkey,
                    @Value("${twitter.consumerSecret:}") String conSecret,
                            @Value("${twitter.accessToken:}") String accessToken,
    @Value("${twitter.accessTokenSecret:}") String accessTokenSecret

    ){
        var conf = new ConfigurationBuilder()
                .setJSONStoreEnabled(true)
                .setOAuthConsumerSecret(conSecret)
                .setOAuthConsumerKey(conkey)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret)
                .build();
        Twitter twitter1 = new TwitterFactory().getInstance();
        twitter = TwitterV2ExKt.getV2(twitter1);
    }


    public int searchTwitter(int userId, int searchId, boolean firstTrial) {

        try {

            DE_TwitterSearch dts = searchRep.getById( searchId, userId);

            return searchTwitter(dts, firstTrial);

        } catch (Exception  e){
            LOGGER.warn("Tweeter Search Not Found");
            return 0;
        }
    }


    // Search and Update Database

    public int searchTwitter(DE_TwitterSearch dts, boolean firstTrial){

        int saved = 0;

        try {

            DE_SeduledTwitterSearch sts = new DE_SeduledTwitterSearch();

            sts.setTwitterSearchId(dts.getId());
            sts.setUserId(dts.getUserId());
            sts.setResultsDate(LocalDate.now());
            int stsId = searchTryRep.create(sts);
            sts.setId(stsId);
            dts.setResultDate(LocalDate.now());
            searchRep.update(dts);

  //          twitter4j.v2.Query query = twitter4j.v2.Query.of(dts.getSearchText());
  //          query = query.count(SEARCH_COUNT);

     //       QueryResult result = twitter.v1().search().search(query);




            TweetsResponse result = twitter.searchRecent(dts.getSearchText(), null, "author_id", SEARCH_COUNT,
            null,null,null,null,null,null,
                    null
                    ,null,null);

  /*          fun searchRecent(
                    query: String,
                    endTime: Date? = null,
                    expansions: String? = null,
                    maxResults: Int? = null,
                    mediaFields: String? = null,
                    nextToken: PaginationToken? = null,
                    placeFields: String? = null,
                    pollFields: String? = null,
                    sinceId: Long? = null,
                    startTime: Date? = null,
                    tweetFields: String? = null,
                    untilId: Long? = null,
                    userFields: String? = null,
    ): */

            dts.setResultDate(LocalDate.now());



            for (Tweet tw: result.getTweets()) {
//                if (!firstTrial) {
                    DE_SearchResult res = new DE_SearchResult();
                    res.setSearchId(dts.getId());
                    res.setPaid(!firstTrial);
                    res.setSeduledSearchNumber(stsId);
                    res.setUserId(dts.getUserId());
                    //   res.setTwitterUserHandle(tw.getUser().getScreenName());
                    res.setTwitterUserId(tw.getAuthorId() == null ? 0 : tw.getAuthorId());
                    res.setTweetId(tw.getId());
                    res.setTweetText(tw.getText());
                    res.setUserId(dts.getUserId());
                    boolean saved1 = resultsRep.create(res) != 0;
                    if (saved1) {
                        saved++;
                    }
//                } else {
    //                saved++; // Fake count for first trial
  //              }
            }

            sts.setNresults(result.getTweets().size());
            sts.setNsent(0);

            LOGGER.warn("SDS: "+sts.getId()+","+stsId+" UserId "+sts.getUserId()+" search id "+sts.getId()+" saved "+saved+
                    " tweet results out of "+result.getTweets().size());
            searchTryRep.update(stsId, sts);


        } catch (TwitterException te){
            LOGGER.warn("Exception searching twetter ",te);
        } catch (Exception te){
            LOGGER.warn("Database Exception searching twetter ",te);
        }
        return saved;
    }

}