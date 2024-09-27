package com.smokpromotion.SmokProm.config.admin;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@Profile({"admin"})
@ControllerAdvice
class AdminDefaultExceptionHandler {
  public static final String DEFAULT_ERROR_VIEW = "admin/error";

  private @Value("${Majorana_SHOW_EXCEPTION:false}") boolean showException;

  private static final Logger LOGGER = LoggerFactory.getLogger(AdminDefaultExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  String showBadRequestPage(Exception e){
    LOGGER.warn("showBadRequestPage: caught exception",e);
    return "admin/private/bad_request";
  }

 // @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
 // @ResponseStatus(HttpStatus.BAD_REQUEST)
 // @Order(Ordered.HIGHEST_PRECEDENCE)
 // String showBadRequestPageMethodNotSupport(Exception e){
 //   LOGGER.warn("showBadRequestPageMethodNotSupport: caught exception",e);
 //   return "admin/private/bad_request";
 // }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  String showBadRequestPageMethodArgument(Exception e){
    LOGGER.warn("showBadRequestPageMethodArgument: caught exception",e);
    return "admin/private/bad_request";
  }

//  @ExceptionHandler(NoHandlerFoundException.class)
//  @ResponseStatus(HttpStatus.NOT_FOUND)
//  @Order(Ordered.HIGHEST_PRECEDENCE)
//  String showNotFoundRequestPage(Exception e){
//    LOGGER.warn("showNotFoundRequestPage: caught exception",e);
//    return "admin/private/notfound";
//  }

  @ExceptionHandler(AuthenticationFailedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @Order(Ordered.HIGHEST_PRECEDENCE)
  String showAccessDeniedPage(Exception e){
    LOGGER.warn("showNotFoundRequestPage: caught exception",e);
    return "admin/private/accessDenied";
  }

  @ExceptionHandler(value = Exception.class)
  @Order(Ordered.LOWEST_PRECEDENCE)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ModelAndView
  defaultExceptionHandler(
          Authentication authentication,
          Model model,
          HttpServletRequest req, Exception e) throws Exception {
    // If the exception is annotated with @ResponseStatus rethrow it and let
    // the framework handle it

    // AnnotationUtils is a Spring Framework utility class.
    if (AnnotationUtils.findAnnotation
                (e.getClass(), ResponseStatus.class) != null)
      throw e;
    ModelAndView mav = null;

    try {
      // Otherwise setup and send the user to a default error-view.
      AdminSecurityPrinciple principle = (AdminSecurityPrinciple) (authentication==null ? null : authentication.getPrincipal());
      mav = new ModelAndView();
      mav.addObject("url",req.getRequestURL()) ;
      // log the exception details
      LOGGER.error(String.format("defaultExceptionHandler - handling error: %s", e.getMessage()));
      LOGGER.error(ExceptionUtils.getStackTrace(e));
      LOGGER.error(String.format("url: %s",req.getRequestURL()));
      if (principle!=null) {
        LOGGER.error(String.format("userId: %s",principle.getId()));
        LOGGER.error(String.format("email: %s", principle.getEmail()));
        LOGGER.error(String.format("fullName: %s", principle.getFirstname() + " " + principle.getLastname()));
      } else {
        LOGGER.error("Principle was null");
      }
      if (principle!=null) {
        mav.addObject("email", principle.getEmail());
        mav.addObject("fullName", principle.getFirstname() + " " + principle.getLastname());
      } else {
        mav.addObject("email","");
        mav.addObject("fullName", "");
      }
      model.addAttribute("showException",showException);
      if (showException) {
        model.addAttribute("exceptionName", e.getClass());
        model.addAttribute("exceptionMessage", e.getMessage());
      }
    } catch (NullPointerException e1) {
      LOGGER.error("An error eccoured without any valid session no session ");
      LOGGER.error(ExceptionUtils.getStackTrace(e));
    }

    mav.setViewName(DEFAULT_ERROR_VIEW);
    mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    return mav;
  }
}