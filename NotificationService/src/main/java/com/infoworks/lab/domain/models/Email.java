package com.infoworks.lab.domain.models;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.validation.Email.EmailPattern;

import java.util.Map;

public class Email extends Message {

    private Map<String, Object> properties;
    @EmailPattern
    private String to;
    @EmailPattern
    private String from;
    private String subject;
    private String template;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplate() {
        return template;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
