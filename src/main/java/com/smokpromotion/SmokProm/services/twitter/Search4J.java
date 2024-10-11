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
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.Query;
import twitter4j.v1.QueryResult;
import twitter4j.v1.Status;

import java.time.LocalDate;

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

    private Twitter twitter;

    public Search4J(){
        twitter = Twitter.getInstance();
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
            sts.setUserId(dts.getCreatedByUserid());
            sts.setResultsDate(LocalDate.now());
            int stsId = searchTryRep.create(sts);

            searchRep.update(dts);

            twitter4j.v1.Query query = Query.of(dts.getSearchText());
            query = query.count(SEARCH_COUNT);

            QueryResult result = twitter.v1().search().search(query);

            dts.setResultDate(LocalDate.now());



            for (Status status : result.getTweets()) {
                if (!firstTrial) {
                    DE_SearchResult res = new DE_SearchResult();
                    res.setSearchId(dts.getId());
                    res.setSeduledSearchNumber(stsId);
                    res.setUserId(sts.getCreatedByUserid());
                    res.setTwitterUserHandle(status.getUser().getScreenName());
                    res.setTwitterUserId(status.getUser().getId());
                    res.setTweetId(status.getId());
                    res.setUserId(dts.getCreatedByUserid());
                    boolean saved1 = resultsRep.create(res) != 0;
                    if (saved1) {
                        saved++;
                    }
                } else {
                    saved++; // Fake count for first trial
                }

            }
            sts.setNresults(saved);
            sts.setNsent(0);
//            searchTryRep.update(sts);

            LOGGER.warn(" UserId "+sts.createdByUserid+" search id "+sts.getId()+" saved "+saved+
                    " tweet results out of "+result.getTweets().size());
            searchTryRep.update(sts);

        } catch (TwitterException te){
            LOGGER.warn("Exception searching twetter ",te);
        } catch (Exception te){
            LOGGER.warn("Database Exception searching twetter ",te);
        }
        return saved;
    }

}