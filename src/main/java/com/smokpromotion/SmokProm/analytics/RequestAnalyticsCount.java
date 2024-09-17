package com.smokpromotion.SmokProm.analytics;

public class RequestAnalyticsCount {

    private String page;

    private String email;

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "RequestAnalyticsCount{" +
                "page='" + page + '\'' +
                ", email='" + email + '\'' +
                ", count=" + count +
                '}';
    }
}
