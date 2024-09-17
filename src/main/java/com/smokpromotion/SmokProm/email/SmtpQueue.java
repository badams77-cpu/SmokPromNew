package com.smokpromotion.SmokProm.email;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class SmtpQueue implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpQueue.class);

    private static long CHECK_INTERVAL_MILLIS = 10000;

    private static int RETRY_ATTEMPTS = 3;

    private ConcurrentLinkedQueue<SmtpQueueItem> queue = new ConcurrentLinkedQueue<>();

    private SmtpMailSender sender;

    private Thread sendThread;

    @Autowired
    public SmtpQueue(SmtpMailSender sender){
        this.sender = sender;
        sendThread = new Thread(this);
        sendThread.start();
    }

    public void send(String from, String fromName, String to, String subject, String body) throws Exception {
        SmtpQueueItem item = new SmtpQueueItem();
        item.setFrom(from);
        item.setFromName(fromName);
        item.setSubject(subject);
        item.setTo(to);
        item.setBody(body);
        queue.add(item);
    }

    public void run(){
        while(true){
            SmtpQueueItem item = null;
            try {
                item = queue.poll();
                if (item != null) {
                    sender.send(item.getFrom(), item.getFromName(), item.getTo(), item.getSubject(), item.getBody());
                }
                Thread.sleep(CHECK_INTERVAL_MILLIS);
            } catch (Exception e){
                if (item!=null) {
                    LOGGER.error("run: Error sending mail to" + item.getTo()+" subject: "+item.getSubject(), e);
                    item.setTries(item.getTries() + 1);
                    if (item.getTries() < RETRY_ATTEMPTS) {
                        queue.add(item);
                    }
                }
            }
        }
    }

}


