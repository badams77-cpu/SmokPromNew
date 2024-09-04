package com.smokpromotion.SmokProm.tweeter;

import Distiller.ORM_ACCESS.DbBean;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class ExpireAuth {

    private static String logFile = "logs/expireAuth.log";

    private static int EXPIRE_MINS = 30;
    private PrintWriter logWriter = null;

    private FileWriter fileWriter = null;


    private DbBean dbBean = null;


    public ExpireAuth(){
        try {
            fileWriter = new FileWriter(logFile, true);
            logWriter = new PrintWriter(fileWriter);
            this.dbBean = DbBean.getSingletonLazy();
            dbBean.connect();
        } catch (IOException e){
            System.err.println("Error writing log: "+e.getMessage());
        } catch (Exception e){
            System.err.println("Error connecting to db: "+e.getMessage());
        }
    }

    private void expire(){
        String sql = "DELETE FROM  twitter_oauth1_verifier WHERE DATE_ADD(created, INTERVAL "+EXPIRE_MINS+" MINUTE)<now();";
        try {
            int del = dbBean.updateSQL(sql);
            log("expired "+del+" authorization");
        } catch (SQLException e){
            log("expire SQL error"+e.getMessage());
        }
    }

    public void log(String s){
        if (logWriter!=null){
            logWriter.println(s);
        } else {
            System.err.println(s);
        }
    }

    public static void main(String argv[]){
        ExpireAuth expireAuth = new ExpireAuth();
        expireAuth.expire();
        expireAuth.finalise();
    }

    public void finalise(){
        logWriter.close();

    }

}
