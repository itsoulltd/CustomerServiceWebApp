package com.infoworks.lab.domain.beans.tasks;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.domain.models.Email;
import com.infoworks.lab.rest.models.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
            MimeMessage mimeMsg = emailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMsg
                    , MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED
                    , StandardCharsets.UTF_8.name());
            //
            Context context = new Context();
            context.setVariables(email.getProperties());
            message.setFrom(email.getFrom());
            message.setTo(email.getTo());
            message.setSubject(email.getSubject());
            String html = templateEngine.process(email.getTemplate(), context);
            message.setText(html, true);
            Map<String, ByteArrayResource> attachments = convertIntoResource(email.getAttachments());
            attachments.forEach((fileName, resource) -> {
                try {
                    message.addAttachment(fileName, resource);
                } catch (MessagingException e) {
                    LOG.error(e.getMessage(), e);
                }
            });
            emailSender.send(mimeMsg);
            LOG.info("Email Sent: {} with html body: {}", email, html);
        } catch (MessagingException | RuntimeException e) {
            LOG.error(e.getMessage());
            return new Response().setStatus(500).setError(e.getMessage());
        }
        return new Response().setStatus(200).setMessage("Email Sent");
    }

    private Map<String, ByteArrayResource> convertIntoResource(Map<String, String> attachments) {
        if (attachments.isEmpty()) return new HashMap<>();
        Map<String, ByteArrayResource> result = new HashMap<>();
        attachments.forEach((fileName, filePath) -> {
            try {
                Path resourcePath = Paths.get(filePath);
                if (Files.exists(resourcePath)
                        && Files.isReadable(resourcePath)){
                    FileInputStream fis = new FileInputStream(resourcePath.toFile());
                    byte[] bytes = new byte[fis.available()];
                    fis.read(bytes);
                    ByteArrayResource resource = new ByteArrayResource(bytes);
                    result.put(fileName, resource);
                    fis.close();
                }
            } catch (Exception e) { LOG.error(e.getMessage(), e); }
        });
        return result;
    }

    @Override
    public Response abort(Email email) throws RuntimeException {
        return new Response().setStatus(500).setError("Exception In SendEmail");
    }
}
