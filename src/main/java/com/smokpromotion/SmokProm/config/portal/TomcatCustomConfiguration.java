package com.smokpromotion.SmokProm.config.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Profile("smok_app")
@Configuration
public class TomcatCustomConfiguration {

    private static final int DEFAULT_MAX_HTTP_PARAMETERS = 50000;

    private static final String ENV_KEY_MAX_PARAMETERS = "Majorana_MAX_HTTP_PARAMETERS";

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatCustomConfiguration.class);

    @Autowired
    private Environment environment;

    /*
    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {

        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();

        int maxParameters = DEFAULT_MAX_HTTP_PARAMETERS;
        String propValue = environment.getProperty(ENV_KEY_MAX_PARAMETERS);
        try {

            if (propValue!=null){
                maxParameters = Integer.parseInt(propValue);
            }
        } catch (NumberFormatException e){
            LOGGER.warn("TomcatCustomConfiguration: env key "+ENV_KEY_MAX_PARAMETERS+" has no integer value "+propValue+" using default "+maxParameters);
        }
        final int finalMaxParameters = maxParameters;
        tomcatFactory.addConnectorCustomizers(connector -> connector.setMaxParameterCount(finalMaxParameters));

        return tomcatFactory;
    }

     */
}