package com.infoworks.lab.domain.beans.tasks;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.domain.models.Email;
import com.infoworks.lab.rest.models.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

public class SendEmail extends AbstractTask<Email, Response> {

    private static Logger LOG = LoggerFactory.getLogger(SendEmail.class);

    private JavaMailSender emailSender;
    private SpringTemplateEngine templateEngine;

    public void setEmailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public SendEmail() {}

    public SendEmail(Email message) {
        super(message);
    }

    @Override
    public Response execute(Email email) throws RuntimeException {
        if (email == null){
            email = getMessage();
        }
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message
                    , MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED
                    , StandardCharsets.UTF_8.name());
            //
            Context context = new Context();
            context.setVariables(email.getProperties());
            helper.setFrom(email.getFrom());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            String html = templateEngine.process(email.getTemplate(), context);
            helper.setText(html, true);
            emailSender.send(message);
            LOG.info("Email Sent: {} with html body: {}", email, html);
        } catch (MessagingException | RuntimeException e) {
            LOG.error(e.getMessage());
            return new Response().setStatus(500).setError(e.getMessage());
        }
        return new Response().setStatus(200).setMessage("Email Sent");
    }

    @Override
    public Response abort(Email email) throws RuntimeException {
        return new Response().setStatus(500).setError("Exception In SendEmail");
    }
}
