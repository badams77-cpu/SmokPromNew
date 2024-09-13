package com.smokpromotion.SmokProm.config.portal;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("smok_app1")
@Component
public class TomcatCustomizerV1 {// implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
/*
    private static int maxfileSize;

    public TomcatCustomizer(@Value("${Majorana_CASH_SHEET_MAX_ATTACH_SIZE_KB:10240}") int maxFileSize){
        this.maxfileSize = maxFileSize;
    }


    @Override
    public void customize(TomcatServletWebServerFactory factory) {

        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                if(connector.getProtocolHandler() instanceof AbstractHttp11Protocol) {
                    ((AbstractHttp11Protocol <?>) connector.getProtocolHandler()).setMaxSwallowSize(maxfileSize*2*1024);
                    ((AbstractHttp11Protocol <?>) connector.getProtocolHandler()).setMaxSavePostSize(maxfileSize*2*1024);
                }
            }
        });
    }
*/
}
