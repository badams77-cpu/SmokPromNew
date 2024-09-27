package com.smokpromotion.SmokProm.util;

import org.springframework.http.ResponseCookie;

import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;


public class CookieFactory {

    private static final String SAME_SITE_STRICT = "Strict";
    private static final int COOKIE_AGE = 7 * 60 * 60;

    private static String cookieDomain = "localhost";

    public static void setCookieDomain(String s){
        cookieDomain = s;
    }

    public static String getCookieDomain(){
        return cookieDomain;
    }

    //    public static Cookie makeCookie(CookieUsing theClass, String name, String value){
    //        Cookie cookie = new Cookie(name, value.replace(" ","_"));
    //        cookie.setMaxAge(theClass.getCookieAge());
    //        cookie.setPath(theClass.getCookiePath());
    //        cookie.setHttpOnly(true);
    //        cookie.setDomain(cookieDomain);
    //        cookie.setSecure(true);
    //        return cookie;
    //    }

    /**
     * Adds a Set-Cookie header to the response, for the given cookie name/value
     * @param response response to add the cookie header to
     * @param theClass instance of CookieUsing being used
     * @param name cookie name
     * @param value cookie value
     */
    public static void AddResponseCookieHeader(HttpServletResponse response,
                                               CookieUsing theClass,
                                               String name,
                                               String value
                                               ) {
        ResponseCookie cookie = makeCookie(theClass, name, value);
        if (cookie != null) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
    }

    /**
     * Creates a ResponseCookie for the given cookie name/value.
     * @param theClass instance of CookieUsing being used
     * @param name cookie name
     * @param value cookie value
     * @return q ResponseCookie with secure, httpOnly, same site and other settings
     */
    public static ResponseCookie makeCookie(CookieUsing theClass, String name, String value){
        ResponseCookie cookie = ResponseCookie.from(name, value.replace(" ","_"))
                                    .maxAge(theClass.getCookieAge())
                                    .path(theClass.getCookiePath())
                                    .httpOnly(true)
                                    .domain(cookieDomain)
                                    .secure(true)
                                    .sameSite(SAME_SITE_STRICT)
                                    .build()
                                    ;
        return cookie;
    }

}
