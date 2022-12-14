package com.infoworks.lab.domain.beans.tasks.OtpTasks;

import com.google.common.base.Strings;
import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.domain.beans.strategies.OtpGenerateStrategy;
import com.infoworks.lab.domain.models.Otp;
import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.sql.query.models.Property;

import java.security.SecureRandom;
import java.time.Duration;

public class GenerateOTP extends AbstractTask<Message, Otp> implements OtpGenerateStrategy<Otp> {

    public GenerateOTP() {this(4, Duration.ofMinutes(5));}

    public GenerateOTP(int length, Duration duration) {
        super(new Property("length", length)
                , new Property("duration", duration.toMillis()));
    }

    @Override
    public Otp execute(Message message) throws RuntimeException {
        int length = Integer.valueOf(getPropertyValue("length").toString());
        long duration = Long.valueOf(getPropertyValue("duration").toString());
        Otp otp = generateOtp(length, duration);
        return (Otp) otp.setStatus(200).setMessage("Length:"+length+"; duration:"+duration);
    }

    @Override
    public Otp abort(Message message) throws RuntimeException {
        return (Otp) new Otp().setStatus(500).setError("Exception In GenerateOTP");
    }

    public Otp generateOtp(int length, long duration) {
        // Adding 0 to boundary as secure random's upper bound is exclusive
        String boundaryString = Strings.padEnd("1", length + 1, '0');
        int boundary = Integer.parseInt(boundaryString);
        //pseudo-random strategy:
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(boundary);
        String value = String.format("%0" + length + "d", num);
        Otp otp = new Otp(value, Duration.ofMillis(duration));
        return otp;
    }

}
