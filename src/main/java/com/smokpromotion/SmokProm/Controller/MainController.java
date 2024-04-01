package com.smokpromotion.SmokProm.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
public class MainController {

    @RequestMapping(path="/public/index.html")
    public String index(Model m, WebRequest w) {
        HttpHeaders headers = new HttpHeaders();

        //    if (ex instanceof UserNotFoundException) {
        //        HttpStatus status = HttpStatus.NOT_FOUND;
        //        User`NotFoundException unfe = (UserNotFoundException) ex;

        //         return handleUserNotFoundException(unfe, headers, status, request);
        //     } else if (ex instanceof ContentNotAllowedException) {
        //         HttpStatus status = HttpStatus.BAD_REQUEST;
        //         ContentNotAllowedException cnae = (ContentNotAllowedException) ex;

        String baldy = " Internal Server Exception ";

//        setModel(m, ex);

        return "/portal/public/home.html";
        //    } else {
        //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        //        return handleExceptionInternal(ex, null, headers, status, request);
        //    }
    }




    @RequestMapping(path="/public/home")
    public String home(Model m, WebRequest w) {
        HttpHeaders headers = new HttpHeaders();
        //    if (ex instanceof UserNotFoundException) {
        //        HttpStatus status = HttpStatus.NOT_FOUND;
        //        User`NotFoundException unfe = (UserNotFoundException) ex;

        //         return handleUserNotFoundException(unfe, headers, status, request);
        //     } else if (ex instanceof ContentNotAllowedException) {
        //         HttpStatus status = HttpStatus.BAD_REQUEST;
        //         ContentNotAllowedException cnae = (ContentNotAllowedException) ex;

        String baldy = " Internal Server Exception ";

//        setModel(m, ex);

        return "/private/portal/home.html";
        //    } else {
        //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        //        return handleExceptionInternal(ex, null, headers, status, request);
        //    }
    }

}
