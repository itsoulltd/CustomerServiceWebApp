package com.infoworks.lab.services;

import com.infoworks.lab.domain.beans.TaskExecutionLogger;
import com.infoworks.lab.domain.models.Email;
import com.infoworks.lab.services.impl.EmailSenderService;
import com.infoworks.lab.webapp.NotificationApi;
import com.infoworks.lab.webapp.config.ThymeleafTemplateConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {NotificationApi.class, ThymeleafTemplateConfig.class, EmailSenderService.class
        , TaskExecutionLogger.class, AnnotationAwareAspectJAutoProxyCreator.class})
@TestPropertySource(locations = {"classpath:smtp-test-config.properties"})
public class EmailSenderServiceTest {

    @Before
    public void setUp() throws Exception {
        //TODO:
    }

    @Autowired
    private EmailSenderService senderService;

    @Test
    public void sendHtmlMessage() throws MessagingException {
        System.out.println("");
        Email email = new Email();
        email.setTo("towhidul.islam@gmail.com");
        email.setFrom("m.towhid.islam@gmail.com");
        email.setSubject("Welcome Email from Infoworks");
        email.setTemplate("welcome-email-sample.html");
        //
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "Moshfiqur Rahman");
        properties.put("subscriptionDate", LocalDate.now().toString());
        properties.put("technologies", Arrays.asList("SpringBoot", "Thymeleaf", "ActiveMQ"));
        email.setProperties(properties);
        //
        senderService.sendHtmlMessage(email);
    }

    @Test
    public void sendWelcomeHtmlMessage() throws MessagingException {
        System.out.println("");
        Email email = new Email();
        email.setTo("towhidul.islam@gmail.com");
        email.setFrom("m.towhid.islam@gmail.com");
        email.setSubject("Welcome From INfoworks");
        email.setTemplate("welcome-email.html");
        //
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "Moshfiqur Rahman");
        properties.put("regDate", LocalDate.now().toString());
        properties.put("defaultPass", "010167");
        email.setProperties(properties);
        //
        senderService.sendHtmlMessage(email);
    }

}