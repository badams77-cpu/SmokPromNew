package com.smokpromotion.SmokProm.services.twitter;


import com.smokpromotion.SmokProm.domain.entity.*;
import com.majorana.maj_orm.ORM_ACCESS.*;
import com.majorana.maj_orm.ORM_ACCESS.DbBean;


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

    // Can now autowire
    private CreateTweet tweeter = null;


    public DistillerTweeter(){
        try {
            fileWriter = new FileWriter(logFile);
            logWriter = new PrintWriter(fileWriter);
            this.dbBean = new DbBean();
            dbBean.connect();
        } catch (IOException e){
            System.err.println("Error writing log: "+e.getMessage());
        } catch (Exception e){
            System.err.println("Error connecting to db: "+e.getMessage());
        }
//        tweeter = new CreateTweet(this);
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








  //  private void tweet(String s){



        // System.out.println(s+"----");
    //    tweeter.tweet(s);
   // }

    public static void main(String argv[]){
        DistillerTweeter dt = new DistillerTweeter();
//        dt.dayPosts();
        dt.finalise();
    }

}
