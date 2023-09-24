package com.smokpromotion.SmokProm.util;

public class MajoranaAwsS3Location {

    private String bucket;
    private String prefix;

    public MajoranaAwsS3Location(String bucket, String prefix){
        this.bucket = bucket;
        this.prefix = prefix;
    }

    public String getBucket() {
        return bucket;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return "MPCAwsS3Location{" +
                "bucket='" + bucket + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }

}
