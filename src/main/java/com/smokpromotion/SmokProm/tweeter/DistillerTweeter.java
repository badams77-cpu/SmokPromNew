package com.smokpromotion.SmokProm.tweeter;


import Distiller.ORM_ACCESS.*;
import Distiller.entities.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class DistillerTweeter {

    public static int N_Articles=6;

    public static int TWEET_LENGTH = 280;

    private static DateTimeFormatter FORMAT = DateTimeFormatter.ISO_DATE_TIME;

    private static String logFile = "logs/distillerTweeter.log";

    private static String FDURL= "https://www.feeddistiller.com/blogs";

    private static String FEEDFILE = "/feed.html";

    private PrintWriter logWriter = null;

    private FileWriter fileWriter = null;

    private CreateTweet tweeter = null;


    public DistillerTweeter(){
        try {
            fileWriter = new FileWriter(logFile);
            logWriter = new PrintWriter(fileWriter);
            this.dbBean = DbBean.getSingletonLazy();
            dbBean.connect();
        } catch (IOException e){
            System.err.println("Error writing log: "+e.getMessage());
        } catch (Exception e){
            System.err.println("Error connecting to db: "+e.getMessage());
        }
        tweeter = new CreateTweet(this);
    }

    public void log(String s){
        if (logWriter!=null){
            logWriter.println(s);
        } else {
            System.err.println(s);
        }
    }

    public void logEx(Throwable e){
        if (e==null){ return; }
        if (logWriter!=null){
            e.printStackTrace(logWriter);
        } else {
            e.printStackTrace(System.out);
        }
    }


    public void finalise(){
        logWriter.close();

    }

    public static int days = 7;

    private DbBean dbBean = null;


    public String getTweetForFeed(Feed feed){
        LocalDateTime then = LocalDate.now().minusDays(days).atStartOfDay();
        ZonedDateTime zdt = then.atZone(ZoneId.systemDefault());
        String time = ""+zdt.toEpochSecond();
       String sql = "SELECT "+ Article.fields + " FROM articles where feedid=? and datestamp>? ORDER BY datestamp ASC LIMIT ?;";
       try {
//           List<Article> arts = new LinkedList<>();
           List<Article> arts = dbBean.getBeansArray(Article.class, sql, new Object[]{ feed.getFeedid(), time, N_Articles} );
           if (arts.size()>0){
               StringBuffer bufmain = new StringBuffer();
               String HEAD = "New Articles in Feed: "+feed.getFeedname()+"\n";
               String TAIL = "\n"+FDURL+feed.getFeedname().replaceAll("\\s","%20");
               String ADVERT = "\nGet FD Reader App (RSS/ATOM) with Twitter posting at https://tinyurl.com/FDReader";

               for(Article art: arts){
                   StringBuffer buf = new StringBuffer();
                    String a = art.getTitle();
                    if (a.length()<50){
                        buf.append(a);
                        buf.append("\n");
                    } else {
                        buf.append(a.substring(0,49));
                        buf.append("...\n");
                    }
                    if (HEAD.length()+TAIL.length()+bufmain.length()+buf.length()+ADVERT.length()<TWEET_LENGTH){
                        bufmain.append(buf.toString());
                    }
               }
               return HEAD+bufmain.toString()+TAIL+ADVERT;
           } else {
               return null;
           }
       } catch (SQLException e){
           log("Exception "+e+" reading articles, "+e.getMessage());
           return null;
       }
    }

    private List<Feed> feedToday(){
        int day = LocalDate.now().getDayOfWeek().getValue()-1;
        String sql = " SELECT "+Feed.fields+" FROM feeds where MOD(feedid,7)=? ";
        try {
//            return new LinkedList<>();
            return dbBean.getBeansArray(Feed.class, sql, new Object[]{ day});
        } catch (SQLException e){
            log("Exception "+e+" reading feeds, "+e.getMessage());
        }
        return new LinkedList();
    }

    private void dayPosts(){
        List<Feed> myFeeds = feedToday();
        log("Found: "+myFeeds.size()+" feeds");
        for(Feed feed : myFeeds){
            String tweet = getTweetForFeed(feed);
            if (tweet!=null){
                tweet(tweet);
            }
        }

    }

    private void tweet(String s){



        // System.out.println(s+"----");
        tweeter.tweet(s);
    }

    public static void main(String argv[]){
        DistillerTweeter dt = new DistillerTweeter();
        dt.dayPosts();
        dt.finalise();
    }

}
