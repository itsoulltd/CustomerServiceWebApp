package com.infoworks.lab.services;

import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.domain.beans.TaskExecutionLogger;
import com.infoworks.lab.domain.beans.tasks.OtpTasks.ConvertOtpIntoSms;
import com.infoworks.lab.domain.beans.tasks.OtpTasks.GenerateOTP;
import com.infoworks.lab.domain.beans.tasks.OtpTasks.StoreOtpInCache;
import com.infoworks.lab.domain.beans.tasks.OtpTasks.VerifyOtpFromCache;
import com.infoworks.lab.domain.beans.tasks.SendSMS;
import com.infoworks.lab.domain.models.Otp;
import com.infoworks.lab.domain.models.Sms;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.services.impl.OtpVerificationService;
import com.infoworks.lab.webapp.NotificationApi;
import com.infoworks.lab.webapp.config.BeanConfig;
import com.it.soul.lab.data.base.DataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {NotificationApi.class, BeanConfig.class, OtpVerificationService.class
        , TaskExecutionLogger.class, AnnotationAwareAspectJAutoProxyCreator.class})
public class OtpVerificationServiceTest {

    @Before
    public void setUp() throws Exception {
        System.out.println("MemCache Size: " + cache.size());
    }

    @Autowired
    private OtpVerificationService service;

    @Autowired
    private DataSource<String, Otp> cache;

    @Test
    public void verify() {
        System.out.println("Loaded: " + (service != null ? "true" : "false"));
        Otp otp = service.storeOtp("01712645571");
        Assert.assertTrue(otp.getStatus() == 200);
        boolean isVerified = service.verify(otp, "01712645571");
        Assert.assertTrue(isVerified);
    }

    @Test
    public void storeOtp() {
        Otp otp = service.storeOtp("01712645572");
        Assert.assertTrue(otp.getStatus() == 200);
        Otp checked = cache.remove("01712645572");
        Assert.assertTrue(otp.equals(checked));
    }

    @Test
    public void expireTest(){
        Assert.assertTrue(true);
    }

    @Test
    public void convertOtpTest() {
        //
        Sms smsRes = new Sms();
        GenerateOTP genOtp = new GenerateOTP(4, Duration.ofMinutes(2));
        StoreOtpInCache storeOtp = new StoreOtpInCache("01712645571", cache);
        ConvertOtpIntoSms convert = new ConvertOtpIntoSms("01712645571"
                , "01851909001"
                , "Otp: %s");
        //
        TaskStack stack = TaskStack.createSync(true);
        stack.push(genOtp);
        stack.push(storeOtp);
        stack.push(convert);
        stack.commit(true, (sms, status) -> {
            if (status != TaskStack.State.Finished) return;
            if (sms != null && sms instanceof Sms){
                smsRes.unmarshallingFromMap(sms.marshallingToMap(true), true);
            }
        });
        //
        Assert.assertTrue(smsRes.getStatus() == 200);
        System.out.println(smsRes.getMessage());
    }

    @Test
    public void sendOtpTest() {
        //
        Response res = new Response();
        GenerateOTP genOtp = new GenerateOTP(4, Duration.ofMinutes(2));
        StoreOtpInCache storeOtp = new StoreOtpInCache("01712645571", cache);
        ConvertOtpIntoSms convert = new ConvertOtpIntoSms("01712645571"
                , "01851909001"
                , "Otp: %s");
        //
        TaskStack stack = TaskStack.createSync(true);
        stack.push(genOtp);
        stack.push(storeOtp);
        stack.push(convert);
        stack.push(new SendSMS());
        stack.commit(true, (response, status) -> {
            if (status != TaskStack.State.Finished) return;
            if (response != null && response instanceof Response){
                res.unmarshallingFromMap(response.marshallingToMap(true), true);
            }
        });
        //
        Assert.assertTrue(res.getStatus() == 200);
        System.out.println(res.getMessage());
    }

    @Test
    public void resendOtpTest() {
        //Pre-Condition: Otp already generated and come from client:
        GenerateOTP genOtpV = new GenerateOTP(4, Duration.ofMillis(2000));
        Otp code = genOtpV.execute(null);
        StoreOtpInCache store = new StoreOtpInCache("01712645571", cache);
        Otp otp = store.execute(code);
        System.out.println("Before: " + otp.toString());
        //..........................................................
        //Main Process For Resend:
        Response res = new Response();
        //Resend Stack:
        TaskStack stack = TaskStack.createSync(true);
        stack.push(new VerifyOtpFromCache("01712645571", otp, cache));
        stack.push(new GenerateOTP(4, Duration.ofMinutes(2)));
        stack.push(new StoreOtpInCache("01712645571", cache));
        stack.push(new ConvertOtpIntoSms("01712645571", "01851909001", "Otp: %s"));
        stack.push(new SendSMS());
        stack.commit(true, (response, status) -> {
            if (status != TaskStack.State.Finished) return;
            if (response != null && response instanceof Response){
                res.unmarshallingFromMap(response.marshallingToMap(true), true);
            }
        });
        //
        Assert.assertTrue(res.getStatus() == 200);
        System.out.println(res.getMessage());
    }

    @Test
    public void otpExpirationTest() {
        GenerateOTP genOtpV = new GenerateOTP(4, Duration.ofMillis(1000));
        Otp code = genOtpV.execute(null);
        StoreOtpInCache store = new StoreOtpInCache("01712645571", cache);
        Otp otp = store.execute(code);
        Assert.assertTrue(otp.getStatus() == 200);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Find the Otp from Cache
        Otp read = cache.read("01712645571");
        Assert.assertNull(read);
    }

    @Test
    public void otpLengthTest() {
        GenerateOTP genOtpV = new GenerateOTP(4, Duration.ofMillis(1000));
        Otp code = genOtpV.execute(null);
        Assert.assertTrue(code.getStatus() == 200);
        Assert.assertTrue(code.getValue().length() == 4);
        //
        GenerateOTP genOtpV2 = new GenerateOTP(6, Duration.ofMillis(1000));
        Otp code2 = genOtpV2.execute(null);
        Assert.assertTrue(code2.getStatus() == 200);
        Assert.assertTrue(code2.getValue().length() == 6);
    }

    /*@Test
    public void clearOtpCache() {
        System.out.println("Before Size: " + cache.size());
        cache.clear();
        System.out.println("After Size: " + cache.size());
    }*/

}