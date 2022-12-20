package com.infoworks.lab.services;

import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.Row;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class NotifyService {

    private RestTemplate notifyTemplate;

    public NotifyService(@Qualifier("notifyTemplate") RestTemplate notifyTemplate) {
        this.notifyTemplate = notifyTemplate;
    }

    public ResponseEntity<String> sendEmail(String from
            , String to
            , String subject
            , String template
            , Map<String, String> attachments
            , Property...properties) {
        //
        HttpHeaders httpHeaders = new HttpHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("subject", subject);
        body.put("template", template);
        body.put("attachments", Objects.nonNull(attachments) ? attachments : new HashMap<>());
        if (properties.length > 0){
            Row row = new Row();
            row.setProperties(Arrays.asList(properties));
            body.put("properties", row.keyObjectMap());
        }
        HttpEntity<Map> httpEntity = new HttpEntity<>(body, httpHeaders);
        //"http://<domain:port>/api/notify/v1/mail"
        ResponseEntity<String> response = notifyTemplate.exchange("/mail"
                , HttpMethod.POST
                , httpEntity
                , String.class);
        return response;
    }

    public ResponseEntity<Map> generateOtp(String username) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Map<String, Object> body = new HashMap<>();
        HttpEntity<Map> httpEntity = new HttpEntity<>(body, httpHeaders);
        //"http://<domain:port>/api/notify/v1/otp/{username}"
        StringBuilder rootUri = new StringBuilder(((RootUriTemplateHandler)notifyTemplate.getUriTemplateHandler()).getRootUri());
        rootUri.append("/otp/{username}");
        ResponseEntity<Map> response = notifyTemplate.exchange(rootUri.toString()
                , HttpMethod.POST
                , httpEntity
                , Map.class
                , username);
        return response;
    }

    public ResponseEntity<Map> verifyOtp(String username, String otp) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Map<String, Object> body = new HashMap<>();
        HttpEntity<Map> httpEntity = new HttpEntity<>(body, httpHeaders);
        //"http://<domain:port>/api/notify/v1/otp/verify/{user-name}/{otp}"
        StringBuilder rootUri = new StringBuilder(((RootUriTemplateHandler)notifyTemplate.getUriTemplateHandler()).getRootUri());
        rootUri.append("/otp/verify/{username}/{otp}");
        ResponseEntity<Map> response = notifyTemplate.exchange(rootUri.toString()
                , HttpMethod.POST
                , httpEntity
                , Map.class
                , username, otp);
        return response;
    }

}
