package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.models.Email;
import com.infoworks.lab.domain.models.Otp;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.services.definition.iEmailSender;
import com.infoworks.lab.services.definition.iOtpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/v1")
public class NotificationController {

    private iEmailSender emailSender;
    private iOtpService otpService;
    private int attachmentFileSizeInMb = 2;
    private int attachmentTotalSizeInMb = 10;

    public NotificationController(iEmailSender emailSender
            , iOtpService otpService
            , @Value("${app.mail.attachment.individual.file.size}") String fileSize
            , @Value("${app.mail.attachment.total.file.size}") String totalFileSize) {
        this.emailSender = emailSender;
        this.otpService = otpService;
        try {
            this.attachmentFileSizeInMb = Integer.parseInt(fileSize.toLowerCase().replace("mb", ""));
            this.attachmentTotalSizeInMb = Integer.parseInt(totalFileSize.toLowerCase().replace("mb", ""));
        } catch (Exception e) {}
    }

    @PostMapping("/push")
    public ResponseEntity<Response> sendPush() {
        Response response = new Response().setMessage("Hi there! from Push").setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sms")
    public ResponseEntity<Response> sendSms() {
        Response response = new Response().setMessage("Hi there! from Sms").setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/otp/{username}")
    public ResponseEntity<Response> sendOtp(@PathVariable("username") String username) {
        //
        Otp otp = otpService.storeOtp(username);
        if (otp.getStatus() == HttpStatus.OK.value())
            return ResponseEntity.ok(otp);
        else
            return ResponseEntity.status(otp.getStatus()).body(otp);
    }

    @PostMapping("/otp/verify/{username}/{otp}")
    public ResponseEntity<Response> verifyOtp(@PathVariable("username") String username
            , @PathVariable("otp") String otp) {
        //
        boolean isVerified = otpService.verify(new Otp(otp), username);
        if(isVerified)
            return ResponseEntity.ok(new Response()
                    .setMessage("Otp Verification Successful.")
                    .setStatus(HttpStatus.OK.value()));
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response()
                    .setError("Otp Verification Failed.")
                    .setStatus(HttpStatus.UNAUTHORIZED.value()));
    }

    @PostMapping("/mail")
    public ResponseEntity<String> sendEmail(@RequestBody Email email) {
        //
        Email filtered = filterOutLargeAttachments(email);
        int code = emailSender.sendHtmlMessage(filtered);
        return ResponseEntity.status(code).body((code == 200 ? "Email send successful" : "Email dispatch successful"));
    }

    @PostMapping("/mail/otp/{username}")
    public ResponseEntity<String> sendOtpEmail(@PathVariable("username") String username
            , @RequestBody Email email) {
        //
        Otp otp = otpService.storeOtp(username);
        email.setTemplate("email-otp-msg.html");
        email.getProperties().put("name", username);
        email.getProperties().put("otp", otp.getValue());
        Email filtered = filterOutLargeAttachments(email);
        int code = emailSender.sendHtmlMessage(filtered);
        return ResponseEntity.status(code).body((code == 200 ? "Email send successful" : "Email dispatch successful"));
    }

    private Email filterOutLargeAttachments(Email email) {
        AtomicLong totalByteLength = new AtomicLong(0);
        long fileMaxSizeInBytes = 1024 * 1024 * attachmentFileSizeInMb;
        long totalFileMaxSizeInBytes = 1024 * 1024 * attachmentTotalSizeInMb;
        Map<String, String> filtered = new HashMap<>();
        email.getAttachments();
        for (Map.Entry<String, String> entry : email.getAttachments().entrySet()){
            try {
                Path resourcePath = Paths.get(entry.getValue());
                if (Files.exists(resourcePath)){
                    long fileSize = Files.size(resourcePath); //return size in bytes
                    if (fileSize <= fileMaxSizeInBytes){
                        filtered.put(entry.getKey(), entry.getValue());
                        totalByteLength.set(fileSize);
                    }
                }
                if (totalByteLength.get() <= totalFileMaxSizeInBytes)
                    break;
            } catch (Exception e) {
                //LOG.error(e.getMessage(), e);
            }
        }
        //Update Email with FilteredList:
        email.setAttachments(filtered);
        return email;
    }

}
