package com.smokpromotion.SmokProm.form;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailTemplateValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplateValidator.class);


    private final String[] VALID_DOMAINS = {
            "^https?://[^.]+\\.imultipractice\\.com.*",
            "^https://\\{mpcsite\\}.*",
            "^mailto:support@urcompliant\\.com.*",
            "^https?://[^.]+\\.henryschein\\.com.*",
            "^https?://[^.]+\\.henryschein\\.co\\.uk.*",
            "^https?://[^.]+\\.soeidental\\.com.*",
            "^https?://[^.]+\\.softwareofexcellence\\.com.*",
            "^https?://[^.]+\\.softwareofexcellence\\.co\\.uk.*",
            "^https?://www\\.facebook\\.com/people/MPC/100063502964755/.*"
    };

    public EmailTemplateValidator(){

    }

    public boolean isHTML(String body){
        return body.toLowerCase().contains("<html");
    }

    public List<String> validate(String body){
        List<String> errors = new LinkedList<>();
        if (!isHTML(body)){
            return errors;
        }
        try {
            Document doc = Jsoup.parse(body);
            Elements links = doc.select("a[href]");
            for (Element link : links){
                String url = link.attr("href");
                if (!isValidLink(url)){
                    if (!url.startsWith("{")) {
                        errors.add("Is not Valid link: " + url);
                    }
                }
            }
            Elements links1 = doc.select("img[src]");
            for (Element link : links1){
                String url = link.attr("src");
                if (!isValidLink(url)){
                    errors.add("Is not Valid link: "+url);
                }
            }
        } catch (Exception e){
            LOGGER.warn("Exception parsing template",e);
            errors.add("Parsing Template HTML failed");
        }
        return errors;
    }

    private boolean isValidLink(String url){
        // Allow template interpolated linked
        if (url.matches("\\{.*?\\}")){ return true; }
        for(String pat : VALID_DOMAINS){
            Pattern pattern = Pattern.compile(pat);
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()){ return true; }
        }
        return false;
    }
}
