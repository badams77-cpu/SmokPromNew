package com.smokpromotion.SmokProm.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class ErrorAdviceHandler {
    /** Provides handling for exceptions throughout this service. */
    @ExceptionHandler({ Exception.class})
    public final String handleException(Exception ex, Model m, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

    //    if (ex instanceof UserNotFoundException) {
    //        HttpStatus status = HttpStatus.NOT_FOUND;
    //        UserNotFoundException unfe = (UserNotFoundException) ex;

   //         return handleUserNotFoundException(unfe, headers, status, request);
   //     } else if (ex instanceof ContentNotAllowedException) {
   //         HttpStatus status = HttpStatus.BAD_REQUEST;
   //         ContentNotAllowedException cnae = (ContentNotAllowedException) ex;

        String baldy = " Internal Server Exception ";

        setModel(m, ex);

        return "/private/portal/error.html";
    //    } else {
    //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    //        return handleExceptionInternal(ex, null, headers, status, request);
    //    }
    }

    @ExceptionHandler({ AccessDeniedException.class})
    public final String handleForbid(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Model m,
                                     AccessDeniedException exc) throws IOException, ServletException  {
        HttpHeaders headers = new HttpHeaders();

        //    if (ex instanceof UserNotFoundException) {
        //        HttpStatus status = HttpStatus.NOT_FOUND;
        //        UserNotFoundException unfe = (UserNotFoundException) ex;

        //         return handleUserNotFoundException(unfe, headers, status, request);
        //     } else if (ex instanceof ContentNotAllowedException) {
        //         HttpStatus status = HttpStatus.BAD_REQUEST;
        //         ContentNotAllowedException cnae = (ContentNotAllowedException) ex;

        String baldy = " Internal Server Exception ";

        setModel(m, exc);

        return "/private/portal/403.html";
        //    } else {
        //        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        //        return handleExceptionInternal(ex, null, headers, status, request);
        //    }
    }


    protected void setModel(Model m, Exception e){
        m.addAttribute("Exception ",e.getMessage());
        m.addAttribute("Cause ",e.getCause().getMessage());
    }

    /** Customize the response for UserNotFoundException. */
  //  protected ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
  //      List<String> errors = Collections.singletonList(ex.getMessage());
  //      return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
  //  }

    /** Customize the response for ContentNotAllowedException. */
   // protected ResponseEntity<ApiError> handleContentNotAllowedException(ContentNotAllowedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
   //     List<String> errorMessages = ex.getErrors()
   ///             .stream()
   //             .map(contentError -> contentError.getObjectName() + " " + contentError.getDefaultMessage())
    //            .collect(Collectors.toList());

    //    return handleExceptionInternal(ex, new ApiError(errorMessages), headers, status, request);
    }

    /** A single place to customize the response body of all Exception types. */
//    protected ResponseEntity<ApiError> handleExceptionInternal(Exception ex, ApiError body, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
//            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
//        }

//        return new ResponseEntity<>(body, headers, status);
//    }
//}