package com.infoworks.lab.services.impl;

import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.domain.beans.tasks.SendEmail;
import com.infoworks.lab.domain.models.Email;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.services.definition.iEmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class EmailSenderService implements iEmailSender {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final TaskQueue taskQueue;

    @Value("${app.mail.dispatch.on.queue}")
    private boolean mailRunOnQueue;

    public EmailSenderService(JavaMailSender emailSender
            , SpringTemplateEngine templateEngine
            , TaskQueue taskQueue) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.taskQueue = taskQueue;
    }

    public int sendHtmlMessage(Email email) {
        if (mailRunOnQueue) {
            SendEmail send = new SendEmail(email);
            taskQueue.add(send);
            return 200;
        } else {
            SendEmail send = new SendEmail();
            send.setEmailSender(emailSender);
            send.setTemplateEngine(templateEngine);
            Response response = send.execute(email);
            return response.getStatus();
        }
    }
}
