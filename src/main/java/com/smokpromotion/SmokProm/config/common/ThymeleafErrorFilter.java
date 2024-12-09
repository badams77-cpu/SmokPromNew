package com.smokpromotion.SmokProm.config.common;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.util.NestedServletException;
import org.thymeleaf.exceptions.TemplateEngineException;

/**
 * Filter to catch Thymeleaf template errors
 */
public class ThymeleafErrorFilter implements Filter {

    Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(ThymeleafErrorFilter.class);

    @Override
    public void init(final FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (final NestedServletException nse) {
            if(nse.getCause() instanceof TemplateEngineException) {
                //Do stuff here
                LOGGER.error("Template engine exception", nse.getCause());
            }

            throw nse;
        }
    }

    @Override
    public void destroy() {
    }
}
