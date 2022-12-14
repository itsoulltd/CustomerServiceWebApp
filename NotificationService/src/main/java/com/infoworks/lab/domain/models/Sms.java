package com.infoworks.lab.domain.models;

import com.infoworks.lab.rest.models.Response;

public class Sms extends Response {

    private String to;
    private String from;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
