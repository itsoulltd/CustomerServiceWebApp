package com.infoworks.lab.webapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.util.services.iResourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Bean("HelloBean")
    public String getHello(){
        return "Hi Spring Hello";
    }

    @Bean
    ObjectMapper getMapper(){
        return Message.getJsonSerializer();
    }

    @Bean
    public iResourceService getResourceService(){
        return iResourceService.create();
    }

    @Bean("notifyTemplate")
    public RestTemplate getNotifyTemplate(@Value("${app.notify.url}") String url) {
        return new RestTemplateBuilder()
                .rootUri(url)
                .build();
    }

}
