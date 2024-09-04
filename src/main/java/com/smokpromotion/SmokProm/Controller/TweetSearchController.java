package com.smokpromotion.SmokProm.controller;



import com.smokpromotion.SmokProm.Exceptions.UserNotFoundException;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;



public class TweetSearchController extends BaseLogedInController {


    private static final String PATH_PREFIX = "portal/private/";

    @RequestMapping(path = "/signedin/add-tweet-search")
    public String home(Model m, WebRequest w, Authentication auth) {
        HttpHeaders headers = new HttpHeaders();
        PortalSecurityPrinciple princ = getPrincipal(auth);
        S_User user = null;
        try {
            user = getUser(princ);
        } catch (Exception ex) {
            if (ex instanceof UserNotFoundException)
                if (ex instanceof UserNotFoundException) {
                    HttpStatus status = HttpStatus.UNAUTHORIZED;
                    UserNotFoundException unfe = (UserNotFoundException) ex;

                    return handleUserNotFoundException(unfe, m);
                }
//        setModel(m, ex);

            return "/portal/private/home.html";
            //    } else {
            //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            //        return handleExceptionInternal(ex, null, headers, status, request);
            //    }
        }

    }


    @RequestMapping(path = "/signedin/list-searches")
    public String listSearches(Model m, WebRequest w, Authentication auth) {
        HttpHeaders headers = new HttpHeaders();
        PortalSecurityPrinciple princ = getPrincipal(auth);
        S_User user = null;
        try {
            user = getUser(princ);
        } catch (Exception ex) {
            if (ex instanceof UserNotFoundException)
                if (ex instanceof UserNotFoundException) {
                    HttpStatus status = HttpStatus.UNAUTHORIZED;
                    UserNotFoundException unfe = (UserNotFoundException) ex;

                    return handleUserNotFoundException(unfe, m);
                }
        }

//        setModel(m, ex);

        return "/portal/private/home.html";
        //    } else {
        //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        //        return handleExceptionInternal(ex, null, headers, status, request);
        //    }
    }


    @RequestMapping(path = "/signedin/update-tweet-search", method = RequestMethod.POST)
    public String updateTweetSearch(Model m, WebRequest w, Authentication auth) {
        HttpHeaders headers = new HttpHeaders();

        PortalSecurityPrinciple princ = getPrincipal(auth);
        S_User user = null;
        try {
            user = getUser(princ);
        } catch (Exception ex) {
            if (ex instanceof UserNotFoundException)
                if (ex instanceof UserNotFoundException) {
                    HttpStatus status = HttpStatus.UNAUTHORIZED;
                    UserNotFoundException unfe = (UserNotFoundException) ex;

                    return handleUserNotFoundException(unfe, m);
                }

//        setModel(m, ex);

            return "/portal/private/home.html";
            //    } else {
            //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            //        return handleExceptionInternal(ex, null, headers, status, request);
        }
        return PATH_PREFIX + "/unexcepted";
    }


    @RequestMapping(path = "/signedin/add-tweet-search", method = "POST")
    public String postTweetSearch(Model m, WebRequest w, Authentication auth) {
        HttpHeaders headers = new HttpHeaders();

        PortalSecurityPrinciple princ = getPrincipal(auth);
        S_User user = null;
        try {
            user = getUser(princ);
        } catch (Exception ex) {
            if (ex instanceof UserNotFoundException)
                if (ex instanceof UserNotFoundException) {
                    HttpStatus status = HttpStatus.UNAUTHORIZED;
                    UserNotFoundException unfe = (UserNotFoundException) ex;

                    return handleUserNotFoundException(unfe, m);
                }
//        setModel(m, ex);

            return "/portal/private/home.html";
            //    } else {
            //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            //        return handleExceptionInternal(ex, null, headers, status, request);
        }
        return PATH_PREFIX + "/unexcepted";
    }


    @RequestMapping(path = "/signedin/tweet-search/{id}", method = "POST")
    public String viewTweetSearch(Model m, WebRequest w, int id, Authentication auth) {
        HttpHeaders headers = new HttpHeaders();

        PortalSecurityPrinciple princ = getPrincipal(auth);
        S_User user = null;
        try {
            user = getUser(princ);
        } catch (Exception ex) {
            if (ex instanceof UserNotFoundException)
                if (ex instanceof UserNotFoundException) {
                    HttpStatus status = HttpStatus.UNAUTHORIZED;
                    UserNotFoundException unfe = (UserNotFoundException) ex;

                    return handleUserNotFoundException(unfe, m);
                }
//        setModel(m, ex);

            return "/portal/private/home.html";

//        setModel(m, ex);

            return "/portal/private/home.html";
            //    } else {
            //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            //        return handleExceptionInternal(ex, null, headers, status, request);
            //    }
        }

    }

    @RequestMapping(path = "/signedin/edit-tweet-search/{id}", method = RequestMethod.POST)
    public String postTweetSearch(Model m, WebRequest w, int id, Authentication auth) {
        HttpHeaders headers = new HttpHeaders();

        PortalSecurityPrinciple princ = getPrincipal(auth);
        S_User user = null;
        try {
            user = getUser(princ);
        } catch (Exception ex) {
            if (ex instanceof UserNotFoundException)
                if (ex instanceof UserNotFoundException) {
                    HttpStatus status = HttpStatus.UNAUTHORIZED;
                    UserNotFoundException unfe = (UserNotFoundException) ex;

                    return handleUserNotFoundException(unfe, m);
                }
            return PATH_PREFIX + "/unexcepted";

//        setModel(m, ex);

            return "/portal/private/home.html";
            //    } else {
            //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            //        return handleExceptionInternal(ex, null, headers, status, request);
            //    }
        }
    }


    @RequestMapping(path = "/signedin/view-result-tweet-search/{id}", method = RequestMethod.POST)
    public String putTweetSearch(Model m, WebRequest w, int id, Authentication auth) {
        HttpHeaders headers = new HttpHeaders();

        PortalSecurityPrinciple princ = getPrincipal(auth);
        S_User user = null;
        try {
            user = getUser(princ);
        } catch (Exception ex) {
            if (ex instanceof UserNotFoundException) {
                HttpStatus status = HttpStatus.UNAUTHORIZED;
                UserNotFoundException unfe = (UserNotFoundException) ex;

                return handleUserNotFoundException(unfe, m);
            }
            return PATH_PREFIX + "/unexcepted";
        }

//        setModel(m, ex);

            return "/portal/private/home.html";
            //    } else {
            //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            //        return handleExceptionInternal(ex, null, headers, status, request);
            //    }

    }

}