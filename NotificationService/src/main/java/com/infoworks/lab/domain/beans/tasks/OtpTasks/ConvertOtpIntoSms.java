package com.infoworks.lab.domain.beans.tasks.OtpTasks;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.domain.models.Otp;
import com.infoworks.lab.domain.models.Sms;
import com.it.soul.lab.sql.query.models.Property;

public class ConvertOtpIntoSms extends AbstractTask<Otp, Sms> {

    public ConvertOtpIntoSms() {}

    public ConvertOtpIntoSms(String from
            , String to) {
        this(from, to, "%s");
    }

    public ConvertOtpIntoSms(String from
            , String to
            , String body) {
        super(new Property("from", from)
        , new Property("to", to)
        , new Property("body", body));
    }

    @Override
    public Sms execute(Otp otp) throws RuntimeException {
        if (otp == null) {
            throw new RuntimeException("Otp must not null in ConvertOtpIntoSms!");
        }
        //Convert Otp into Sms:
        Sms sms = new Sms();
        sms.setFrom(getPropertyValue("from").toString());
        sms.setTo(getPropertyValue("to").toString());
        sms.setMessage(
                String.format(getPropertyValue("body").toString()
                , otp.getValue())
        );
        return (Sms) sms.setStatus(200);
    }

    @Override
    public Sms abort(Otp otp) throws RuntimeException {
        return (Sms) new Sms().setStatus(500).setError("Exception in ConvertOtpIntoSms");
    }
}
