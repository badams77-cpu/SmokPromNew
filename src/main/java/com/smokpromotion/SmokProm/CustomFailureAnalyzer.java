package com.smokpromotion.SmokProm;

import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;


public class CustomFailureAnalyzer {

     /*
        extends AbstractFailureAnalyzer<ConfigurationPropertiesBindException>  {

    private static final Logger LOGGER = MethodPrefixingLoggerFactory.getLogger(SmokApplication.class);

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, ConfigurationPropertiesBindException cause) {
        LOGGER.warn("Exception e in Starting Spring",rootFailure);
        Throwable cau = cause;
       while(cau !=null){
            LOGGER.warn("Exception caused by Starting Spring",cause);
            cau = cause.getCause();
        }
        return new FailureAnalysis(getDescription(cau), getAction(cau), cau);
    }




        private String getDescription(Throwable ex) {
            return String.format("The bean %s could not be injected as %s "
                            + "because it is of type %s");
        }

        private String getAction(Throwable ex) {
            return String.format("Consider creating a bean with name %s of type %s");
        }

*/
}
