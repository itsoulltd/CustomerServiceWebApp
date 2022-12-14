package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.models.Email;
import com.infoworks.lab.domain.models.Otp;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.services.definition.iEmailSender;
import com.infoworks.lab.services.definition.iOtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class NotificationController {

    private iEmailSender emailSender;
    private iOtpService otpService;

    public NotificationController(iEmailSender emailSender
            , iOtpService otpService) {
        this.emailSender = emailSender;
        this.otpService = otpService;
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
        int code = emailSender.sendHtmlMessage(email);
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
        int code = emailSender.sendHtmlMessage(email);
        return ResponseEntity.status(code).body((code == 200 ? "Email send successful" : "Email dispatch successful"));
    }

}
