package com.smokpromotion.SmokProm.util;

import java.util.Objects;

public class MajoranaAwsAccessCredentials {

    private String accessKey;
    private String secretKey;

    public MajoranaAwsAccessCredentials(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MajoranaAwsAccessCredentials)) return false;
        MajoranaAwsAccessCredentials that = (MajoranaAwsAccessCredentials) o;
        return Objects.equals(getAccessKey(), that.getAccessKey()) && Objects.equals(getSecretKey(), that.getSecretKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccessKey(), getSecretKey());
    }
}
