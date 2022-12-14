package com.infoworks.lab.services.impl;

import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.domain.beans.tasks.OtpTasks.GenerateOTP;
import com.infoworks.lab.domain.beans.tasks.OtpTasks.StoreOtpInCache;
import com.infoworks.lab.domain.beans.tasks.OtpTasks.VerifyOtpFromCache;
import com.infoworks.lab.domain.models.Otp;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.services.definition.iOtpService;
import com.it.soul.lab.data.base.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class OtpVerificationService implements iOtpService {

    private DataSource<String, Otp> cache;
    private static Logger LOG = LoggerFactory.getLogger(OtpVerificationService.class);

    @Value("${app.otp.ttl.minute}")
    private long otpTtlInMinute;

    @Value("${app.otp.length}")
    private int otpLength;

    public OtpVerificationService(@Qualifier("otpCache") DataSource<String, Otp> cache) {
        this.cache = cache;
    }

    public boolean verify(Otp otp, String forKey) {
        VerifyOtpFromCache verify = new VerifyOtpFromCache(forKey, cache);
        Response response = verify.execute(otp);
        LOG.info(response.toString());
        return response.getStatus() == 200 ? true : false;
    }

    public Otp storeOtp(String forKey) {
        Otp otpRes = new Otp();
        GenerateOTP genOtp = new GenerateOTP(otpLength, Duration.ofMinutes(otpTtlInMinute));
        StoreOtpInCache storeOtp = new StoreOtpInCache(forKey, cache);
        //
        TaskStack stack = TaskStack.createSync(true);
        stack.push(genOtp);
        stack.push(storeOtp);
        stack.commit(true, (otp, status) -> {
            if (status != TaskStack.State.Finished) return;
            if (otp != null && otp instanceof Otp){
                otpRes.unmarshallingFromMap(otp.marshallingToMap(true), true);
            }
        });
        return otpRes;
    }
}
