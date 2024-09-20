package com.smokpromotion.SmokProm.analytics.entity;

import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import com.smokpromotion.SmokProm.analytics.AnalyticsSiteEnum;
import com.smokpromotion.SmokProm.config.ExtraParameterException;
import com.smokpromotion.SmokProm.config.MissingParameterException;
import com.smokpromotion.SmokProm.config.admin.AdminSecurityPrinciple;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import org.springframework.web.method.HandlerMethod;

import jakarta.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This Data Object, is construction from the HTTP request and principle (also handler, and execution time),
 * And represent all the data to be sent to mpcadmin for each requst.
 *
 */

public class RequestAnalyticsData extends BaseMajoranaEntity {

    private static final String TABLE_NAME="analytics";


    private static final String MASKED_VALUE = "*********";

    private static final String[] IGNORE_POST_PARAMETERS = {"password", "zipPass"};

    private static final String[] CHANGE_PASSWORD_PARAMETERS = { "current", "neww", "repeat"};

    private static final String[] CHANGE_PASSWORD_ACTIONS = {"/change-password", "/prec/change-password", "/adm/prec/change-password"};

    private static final NumberFormat formatter = new DecimalFormat("#0.000");

    private static final DateTimeFormatter DATE_ONLY_FORMAT = DateTimeFormatter.ofPattern("EEE dd-MMM-yyyy");

    private static final DateTimeFormatter TIME_ONLY_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final int MAX_FIELD_LENGTH = 80;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd'T'HH:mm:ss");

    private static final String[] dataKeys = {"date","get_action", "method","request_body" ,"host", "email", "group_id", "group_name", "uid", "get_type", "all_get","all_post", "postedVar", "browser", "ip", "duration", "token", "site"};

    private static Set<String> keySet = new HashSet<String>();

    static {
        for(String s :dataKeys) {
            keySet.add(s);
        }
    }

    public static final int ACTION_LENGTH = 255;

    private String host;
    private String method;
    private String email;

    private int groupId;
    private String groupName;
    private String action;
    private String type;
    private int uid;
    private String postedVars;
    private String browser;
    private String duration;
    private String queryString;
    private String remoteAddress;
    private String token;
    private LocalDate date;
    private LocalDateTime datetime;
    private String requestBody;
    private AnalyticsSiteEnum site;

    public RequestAnalyticsData(){

    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }


    /**
     * Construtor, extract the data from the http request, principle, handler method and executeTime
     *
     * @param request
     * @param principle
     * @param handler
     * @param executeTime
     */

    public RequestAnalyticsData(HttpServletRequest request, PortalSecurityPrinciple principle, Object handler, long executeTime) {
        extractFromRequest(request, handler);
        setFromPrinciple(principle);
        duration = formatter.format(executeTime/1000.0);
        date = LocalDate.now();
        requestBody ="";
    }


    public RequestAnalyticsData(HttpServletRequest request, AdminSecurityPrinciple principle, Object handler, long executeTime) {
        extractFromRequest(request,  handler);
        setFromAdminPrinciple(principle);
        duration = formatter.format(executeTime/1000.0);
        date = LocalDate.now();
        requestBody ="";
    }

    public RequestAnalyticsData(HttpServletRequest request, PortalSecurityPrinciple principle, Object handler, long executeTime, String body, AnalyticsSiteEnum profile) {
        extractFromRequest(request, handler);
        setFromPrinciple(principle);
        duration = formatter.format(executeTime/1000.0);
        date = LocalDate.now();
        requestBody = body;
        site = profile;
    }

    public RequestAnalyticsData(HttpServletRequest request, AdminSecurityPrinciple principle, Object handler, long executeTime, String body, AnalyticsSiteEnum profile) {
        extractFromRequest(request,  handler);
        setFromAdminPrinciple(principle);
        duration = formatter.format(executeTime/1000.0);
        date = LocalDate.now();
        requestBody = body;
        site =profile;
    }


    private void extractFromRequest(HttpServletRequest request, Object handler) {
        host = request.getServerName();
        method = request.getMethod();
        browser = request.getHeader("User-Agent");
        remoteAddress = request.getRemoteAddr();
        queryString = request.getQueryString();
        action = request.getRequestURI().substring(request.getContextPath().length());
        try {
            HandlerMethod method =  (HandlerMethod)  handler;
            if (method!=null) {
                type = method.toString();
            } else {
                type ="Unknown";
            }
        } catch (ClassCastException e) {
            type = "Unknown";
        }
        if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT") || method.equalsIgnoreCase("PATCH")) {
            Map<String, String[]> allMap = request.getParameterMap();
            postedVars = formatPostValuesMap(action, allMap);
        } else {
            postedVars = "";
        }
    }

    private void setFromPrinciple(PortalSecurityPrinciple principle) {
        if (principle != null) {
            uid = principle.getId();
            email = principle.getEmail();
        } else {
            groupId = 0;
            uid = 0;
            email = "";
            groupName = "";
        }
    }


    private void setFromAdminPrinciple(AdminSecurityPrinciple principle) {
        if (principle != null) {
            uid = principle.getId();
            email = principle.getEmail();
            groupId = 0;
            groupName = "Admin";
        } else {
            groupId = 0;
            uid = 0;
            email = "";
            groupName = "";
        }
    }

    public RequestAnalyticsData(Map<String,String> paramMap){
        action = paramMap.get("get_action");
        // Void Saving to long a string
        if (action!=null && action.length()>ACTION_LENGTH){
            action=action.substring(0, ACTION_LENGTH);
        }
        if (action==null) throw new MissingParameterException("action missing");
        method = paramMap.get("method");
        if (method==null) throw new MissingParameterException("method missing");
        host = paramMap.get("host");
        if (host==null) throw new MissingParameterException("host missing");
        // These 4 parameters may be null for pages not protected by authentication
        email =  paramMap.get("email");
        groupId =  Integer.parseInt( paramMap.getOrDefault("group_id","0"));
        groupName= paramMap.get("group_name");
        uid =  Integer.parseInt( paramMap.getOrDefault("uid","0"));
        // Legacy not used
        type = paramMap.get("get_type");
        // Null for posts
        postedVars = paramMap.get("all_post");
        // Null unless query string sent
        queryString = paramMap.get("all_get");
        browser = paramMap.getOrDefault("browser","");
        if (browser==null) throw new MissingParameterException("browser missing");
        remoteAddress = paramMap.get("ip");
        if (remoteAddress==null) throw new MissingParameterException("ip missing");
        duration = paramMap.get("duration");
        if (duration==null) throw new MissingParameterException("duration missing");
        token = paramMap.get("token");
        String mydate = paramMap.get("date");
        if (duration==null) throw new MissingParameterException("date missing");
        requestBody = paramMap.get("request_body");
        if (requestBody==null) throw new MissingParameterException("body missing");
        site = AnalyticsSiteEnum.GetFromCode(paramMap.get("site"));

        try {
            date = LocalDate.parse(mydate, DateTimeFormatter.ISO_DATE);
        } catch (Exception e){
            throw new MissingParameterException("date unreadable: "+ mydate);
        }
        Set<String> extra=  paramMap.keySet().stream().filter(x->!keySet.contains(x)).collect(Collectors.toSet());
        if (!extra.isEmpty()){ throw new ExtraParameterException("Extra params"+extra.stream().collect(Collectors.joining(","))); }
    }

    private String formatPostValuesMap(String action, Map<String, String[]> map){
        StringBuilder builder = new StringBuilder();
        boolean firstKey = true;
        Set<String> maskKeys = Arrays.stream(IGNORE_POST_PARAMETERS).collect(Collectors.toSet());
        boolean isChangePassword =  Arrays.stream(CHANGE_PASSWORD_ACTIONS).anyMatch(x->action.startsWith(x));
        Set<String> changePasswordMaskKeys = Arrays.stream(CHANGE_PASSWORD_PARAMETERS).collect(Collectors.toSet());
        for (String key : map.keySet()) {
            String[] strArr = (String[]) map.get(key);
            // Replace parameter value with mask pattern
            if (maskKeys.contains(key) || (isChangePassword&& changePasswordMaskKeys.contains(key))) {
                for(int i=0;i<strArr.length; i++){
                    strArr[i] = MASKED_VALUE;
                }
            }
            if (!firstKey) {
                builder.append("&");
            }
            firstKey = false;
            builder.append(key + "=");
            boolean firstVal = true;
            for (String val : strArr) {
                if (!firstVal) {
                    builder.append(",");
                }
                firstVal = false;
                builder.append(val);
            }
        }
        return builder.toString();
    }

    public String getHost() {
        return host;
    }

    public String getMethod() {
        return method;
    }

    public int getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getAction() {
        return action;
    }

    public String getType() {
        return type;
    }


    public String getPostedVars() {
        return postedVars;
    }

    public String getPostedVarsFormatted() {
        return postedVars==null? "": Arrays.stream(postedVars.split("&")).map(x->truncate(x)).collect(Collectors.joining("\n&"));
    }


    public String getBrowser() {
        return browser;
    }

    public String getDuration() {
        return duration;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getQueryStringFormatted() {
        return queryString==null? "": Arrays.stream(queryString.split("&")).map(x->truncate(x)).collect(Collectors.joining("\n&"));
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDateTimeFormatted(){
        return datetime==null ? "" : datetime.format(DATE_FORMAT);
    }

    public String getDateFormatted(){
        return datetime==null ? "" : datetime.format(DATE_ONLY_FORMAT);
    }

    public String getTimeFormatted(){
        return datetime==null ? "" : datetime.format(TIME_ONLY_FORMAT);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestBodyFormatted() {
        return requestBody==null? "": Arrays.stream(requestBody.split(",")).map(x->truncate(x)).collect(Collectors.joining("\n,"));
    }

    public static void setKeySet(Set<String> keySet) {
        RequestAnalyticsData.keySet = keySet;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setPostedVars(String postedVars) {
        this.postedVars = postedVars;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public AnalyticsSiteEnum getSite() {
        return site;
    }

    public void setSite(AnalyticsSiteEnum site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return "RequestAnalyticsData{" +
                "host='" + host + '\'' +
                ", method='" + method + '\'' +
                ", email='" + email + '\'' +
                ", groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", action='" + action + '\'' +
                ", type='" + type + '\'' +
                ", uid=" + uid +
                ", postedVars='" + postedVars + '\'' +
                ", browser='" + browser + '\'' +
                ", duration='" + duration + '\'' +
                ", queryString='" + queryString + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", token='" + token + '\'' +
                ", date=" + date +
                ", datetime=" + datetime +
                ", requestBody='" + requestBody + '\'' +
                ", site='" + site + '\'' +
                '}';
    }

    /**
     *
     * @return LinkedMultiValueMap, the data stored as a map to send via RestTemplate, form-urlencoded
     */

    public LinkedHashMap<String, String> getDataMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("host", host);
        map.put("uid", Integer.toString(uid));
        map.put("method", method);
        map.put("email", email);
        map.put("group_id", Integer.toString(groupId));
        map.put("group_name", groupName);
        map.put("get_action", action);
        map.put("get_type", type);
        map.put("all_post" , postedVars);
        map.put("all_get", queryString);
        map.put("browser", browser);
        map.put("ip",remoteAddress);
        map.put("duration", duration);
        map.put("token", token);
        map.put("date", date.format(DateTimeFormatter.ISO_DATE));
        map.put("request_body", requestBody);
        if (site!=null){ map.put("site", site.getCode()); }
        return map;
    }

    private String truncate(String s){
        if (s==null){ return ""; }
        if (s.length()<MAX_FIELD_LENGTH){
            return s;
        }
        return s.substring(0,MAX_FIELD_LENGTH);
    }
}
