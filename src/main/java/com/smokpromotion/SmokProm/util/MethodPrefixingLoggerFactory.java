package com.smokpromotion.SmokProm.util;

public class MethodPrefixingLoggerFactory {

    public static MethodPrefixingLogger getLogger(Class target){
        return new MethodPrefixingLogger(target);
    }

}
