package com.smokpromotion.SmokProm.mail_service;

import com.urcompliant.domain.EmailLanguage;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.entity.DE_EmailTemplate;
import com.urcompliant.domain.repository.DR_EmailTemplate;
import com.urcompliant.form.EmailTemplateValidator;
import com.urcompliant.util.GenericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class SmtpMailSender {



    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpMailSender.class);
    private static final String ERROR_MESSAGE = "An error occurred while sending the email" + System.lineSeparator() + "Exception messsage: ";
    private static final String MISSING_INFO_ERROR_MESSAGE = "The email has not been sent" + System.lineSeparator() + "Missing Mandatory information ";




    @Value("${SMTP_MAIL_USEPASSWORD:true}")
    private boolean smtpUsePassword;

    @Value("${SMTP_MAIL_USERNAME:null}")
    private String SMTP_MAIL_USERNAME;

    @Value("${SMTP_MAIL_PASSWORD:null}")
    private String SMTP_PASSWORD;

    @Value("${SMTP_MAIL_HOSTNAME:null}")
    private String HOST;

    @Value("${SMTP_MAIL_PORT:null}")
    private String PORT;

    private Environment env;

    private DR_EmailTemplate drEmailTemplate;

    private EmailTemplateValidator validator;

    @Autowired
    SmtpMailSender(Environment env, DR_EmailTemplate drEmailTemplate, EmailTemplateValidator validator) {
        this.env = env;
        this.drEmailTemplate = drEmailTemplate;
        this.validator = validator;
    }



    public void send(String from, String fromName, String to, String subject, String body) throws Exception {

        int i = 0;
        LOGGER.debug("Initiating MAIL SEND Process - Recipient:"+to+" -  Subject:"+subject);
        if (!checkCredentials()) {
            LOGGER.debug("Credential form SMTP not valid, email not sent.");
            throw new MessagingException("Invalid SMTP Credential");

        } else if (GenericUtils.isValid(to)) {
            Properties props = getSmtpProperties();
            String login =SMTP_MAIL_USERNAME ;
            String pass = SMTP_PASSWORD;
            Authenticator auth = new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(login,pass);
                }
            };

            Session session = smtpUsePassword?Session.getInstance(props,  auth):Session.getInstance(props);
            try {

                Transport transport = session.getTransport();

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from,fromName));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                message.setContent(body, "text/html; charset=utf-8");
                message.setSubject(subject);
//				message.setText(body);

                Transport.send(message);

                LOGGER.debug("Done");

            } catch (MessagingException e) {
                LOGGER.error(ERROR_MESSAGE +e.getMessage());
                throw new RuntimeException(e);
            } catch (Exception e) {
                LOGGER.error(ERROR_MESSAGE +e.getMessage());
                throw e;
            }

        } else {
            LOGGER.error(MISSING_INFO_ERROR_MESSAGE);
        }

    }

    // only used by sendWithAttachments - which has been disabled (see below)
    //    private String addSignature(String body) {
    //        String a = "";
    //        if (GenericUtils.isValid(body)) {
    //
    //
    //            a+="<html><head><title></title></head><body> "+
    //                    "<br><br><p style='color: blue;'>"+
    //                    "<img src='https://www.imultipractice.com/img/multipractice-cloud.png'><br>"+
    //                    "<span style='font-size:12px;'><span style='font-family: arial,helvetica,sans-serif;'><strong> Multipractice Cloud </strong><br />" + System.lineSeparator()
    //                    + "<br />"
    //               //     + "<em>According to the law 196/2003 the contents of this email message and any attachments are intended solely for the addressee(s)and may contain confidential and/or privileged information and may be legally protected from disclosure. If you are not the intended recipient of this message or if this message has been addressed to you in error, please immediately alert the sender by reply email and then delete this message and any attachments.</em></span></span></p>"
    //                    + "</body>"
    //                    + "</html>";
    //
    //        }
    //        return body+=a;
    //
    //    }

    // appears to be unused - sendAttachmentTemplate is being used for sending emails with attachments, by using a template.
    // If this functionality is required, consider if this method should be re-enabled, or some variant of sendAttachmentTemplate
    //
    //    public void sendWithAttachments(String from, String fromName, String to, String subject, String body, byte[] attachment, String fileName) throws MessagingException {
    //
    //        if (!checkCredentials()) {
    //            LOGGER.debug("Credential form SMTP not valid, email not sent.");
    //            throw new MessagingException("Invalid SMTP Credential");
    //
    //        } else if (GenericUtils.isValid(to)) {
    //
    //            Properties props = getSmtpProperties();
    //            String login = SMTP_MAIL_USERNAME;
    //            String pass = SMTP_PASSWORD;
    //
    //            Authenticator auth = new Authenticator() {
    //                public PasswordAuthentication getPasswordAuthentication() {
    //                    return new PasswordAuthentication(login, pass);
    //                }
    //            };
    //
    //            Session session = smtpUsePassword?Session.getInstance(props,  auth):Session.getInstance(props);
    //            try {
    //
    //                Message message = new MimeMessage(session);
    //                message.setFrom(new InternetAddress(from, fromName));
    //                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    //
    //                message.setSubject(subject);
    //                BodyPart messageBodyPart = new MimeBodyPart();
    //                body = addSignature(body);
    //                messageBodyPart.setContent(body, "text/html; charset=utf-8");
    //                Multipart multipart = new MimeMultipart();
    //                multipart.addBodyPart(messageBodyPart);
    //
    //                messageBodyPart = new MimeBodyPart();
    //                DataSource source = new ByteArrayDataSource(attachment, "application/pdf");
    //                messageBodyPart.setDataHandler(new DataHandler(source));
    //                messageBodyPart.setFileName(fileName);
    //                multipart.addBodyPart(messageBodyPart);
    //
    //
    //                // Send the complete message parts
    //                message.setContent(multipart);
    //
    //                // Send message
    //                Transport.send(message);
    //                LOGGER.debug("Mail Sent Successfully!!! Address:" + SMTP_MAIL_USERNAME + " Subject:" + subject);
    //
    //            } catch (MessagingException e) {
    //                LOGGER.error(ERROR_MESSAGE + e.getMessage());
    //                throw new RuntimeException(e);
    //            } catch (Exception e) {
    //                LOGGER.error(ERROR_MESSAGE + e.getMessage());
    //            }
    //
    //        } else {
    //            LOGGER.debug(MISSING_INFO_ERROR_MESSAGE);
    //
    //        }
    //
    //
    //    }

    /**
     *
     * @param name
     * @param language
     * @param replacementMap
     * @return Pair of Subject and Body
     */
    public BodyAndSubject getMessageTemplate(String name, EmailLanguage language, Map<String, String> replacementMap) throws MessagingException {
        Optional<DE_EmailTemplate> optTemplate = drEmailTemplate.getByNameAndLanguage(PortalEnum.AWS, name, language.getValue());
        if (!optTemplate.isPresent()){
            optTemplate = drEmailTemplate.getByNameAndLanguage(PortalEnum.AWS, name, EmailLanguage.ENGLISH.getValue());
        }
        if (!optTemplate.isPresent()){ throw new MessagingException("Email Template: "+name+" for language "+language+" Does not exist"); }
        DE_EmailTemplate template = optTemplate.get();
        String subject = template.getSubject();
        String body = template.getTemplateBody();
        // Match Pattern {repeatIncome} {incomeXXX} {/repeatIncome}  and replace with as many income1 income2 .. and in the repeatIncome value
        for (Map.Entry<String, String> entry : replacementMap.entrySet()){
            String key = entry.getKey();
            if (key.startsWith("repeat")){
                String pat = "(?s)\\{"+key+"\\}(.*?)\\{/"+key+"\\}";
                Matcher matcher = Pattern.compile(pat).matcher(body);
                if (matcher.find()) {
                    String repeatedText = matcher.group(1);
                    String replace = "";
                    int repCount = 0;
                    try {
                        repCount = Integer.parseInt(entry.getValue());
                    } catch (Exception e) {
                        LOGGER.error("repeat key " + key + " value " + entry.getValue() + " is not an integer");
                    }
                    for (int i = 1; i <= repCount; i++) {
                        replace = replace + repeatedText.replace("XXX", "" + i);
                    }
                    body = body.replaceAll(pat,replace);
                }
            }
        }

        for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
            // We introduced conditional blocks between {ifTag} and {/ifTag}, if the expression is true, remove the tags, if false, remove tags and anything in between
            if (entry.getKey().startsWith("if")){
                if (!entry.getValue().equalsIgnoreCase("false")) {
                    body = body.replaceAll("\\{/?" + entry.getKey() + "\\}", "");
                } else {
                    body = body.replaceAll("(?s)\\{" + entry.getKey() + "\\}"+".*?"+"\\{/" + entry.getKey() + "\\}", "");
                }
            }
        }
        for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
            try {
                subject = subject.replace("{" + entry.getKey() + "}", entry.getValue());
                if (!entry.getKey().startsWith("if") && !entry.getKey().startsWith("repeat")) {
                    body = body.replace("{" + entry.getKey() + "}", entry.getValue());
                }
            } catch(Exception e){
                LOGGER.warn("getMessageTemplate: Error substituting: "+entry.getKey());
            }
        }
        List<String> errors = validator.validate(body);
        if (!errors.isEmpty()){
            throw new MessagingException("Email Template: "+name+" for language "+language+" is not valid, contains links or images from unauthorised domains: "+
                    errors.stream().collect(Collectors.joining(",")));
        }
        return new BodyAndSubject(body, subject);
    }

    /**
     *
     * @param to         dest mail address
     * @param template   string template name
     * @param language    Email Language for template
     * @param replaceMap   Map of strings which when in template with curly bracket will be replaced
     * @return           true if sent
     * @throws MessagingException
     */

    public boolean sendTemplate(String to, String template, EmailLanguage language, Map<String,String> replaceMap) throws MessagingException {
        BodyAndSubject bodyAndSubject = getMessageTemplate(template, language, replaceMap);
        Message message = createMessage(to, bodyAndSubject.getSubject());
        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        Multipart multipart = getMultipartWithBody(bodyAndSubject.getBody(), messageBodyPart);

        message.setContent(multipart);
        // Part two is attachment
        // Send the complete message parts
        sendMessage(to, message, multipart);
        return true;
    }

    public EmailPreview getEmailPreview(String to, String template, EmailLanguage language, Map<String,String> replaceMap) throws MessagingException {
        BodyAndSubject bodyAndSubject = getMessageTemplate(template, language, replaceMap);
        EmailPreview preview = new EmailPreview();
        preview.setBody(bodyAndSubject.getBody());
        preview.setSubject(bodyAndSubject.getSubject());
        preview.setTo(to);
        return preview;
    }


    /**
     *
     * @param to             dest mail address
     * @param attachment     byte[] attachment bytes
     * @param filename       attachment file name
     * @param template       templare name
     * @param language        EmailLanguage for template
     * @param replacementMap   Map of strings which when in template with curly bracket will be replaced
     * @return               true if sent
     * @throws MessagingException
     */

    public boolean sendAttachmentTemplate(String to, byte[] attachment, String filename, String template, EmailLanguage language, Map<String, String> replacementMap) throws MessagingException {
        BodyAndSubject bodyAndSubject = getMessageTemplate(template, language, replacementMap);
        Message message = createMessage(to, bodyAndSubject.getSubject());
        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();
        Multipart multipart = getMultipartWithBody(bodyAndSubject.getBody(), messageBodyPart);

        // Part two is attachment
        MimeBodyPart part = new MimeBodyPart();
        ByteArrayDataSource bds = new ByteArrayDataSource(attachment, "application/zip");
        part.setDataHandler(new DataHandler(bds));
        part.setFileName(bds.getName());
        part.setFileName(filename.replace("csv", "zip"));
        multipart.addBodyPart(part);
        // Send the complete message parts
        sendMessage(to, message, multipart);
        return true;
    }

    private Multipart getMultipartWithBody(String messageText, BodyPart messageBodyPart) throws MessagingException {


        // Now set the actual message
        messageBodyPart.setContent(messageText, "text/html; charset=utf-8");
        // Create a multipar message
        Multipart multipart = new MimeMultipart();
        // Set text message part
        multipart.addBodyPart(messageBodyPart);
        return multipart;
    }

    private Message createMessage(String to, String subject) throws MessagingException {
        Properties props = getSmtpProperties();
        // Get the Session object.
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_MAIL_USERNAME,SMTP_PASSWORD);
            }
        };

        Session session = smtpUsePassword?Session.getInstance(props,  auth):Session.getInstance(props);

        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);
        // Set From: header field of the header.
        message.setFrom(new InternetAddress(SMTP_MAIL_USERNAME));
        // Set To: header field of the header.
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
        // Set Subject: header field
        message.setSubject(subject);
        return message;
    }

    private void sendMessage(String to, Message message, Multipart multipart) throws MessagingException {
        message.setContent(multipart);
        // Send message
        Transport.send(message);
        LOGGER.debug("Message sent successfully to " + to);
    }


    private boolean checkCredentials() {
        boolean ret = true;
        if ("null".equals(SMTP_MAIL_USERNAME) ||"null".equals(SMTP_PASSWORD) || "null".equals(HOST) || "null".equals(HOST) ) {
            ret = false;
        }

        return ret;
    }


    private Properties getSmtpProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.socketFactory.port", PORT);
        props.put("mail.smtp.socketFactory.class",  "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", smtpUsePassword);
        props.put("mail.smtp.port", PORT);

        return props;
    }



}

class BodyAndSubject {

    private final String body;
    private final String subject;

    public BodyAndSubject(String body, String subject){
        this.body = body;
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }
}
