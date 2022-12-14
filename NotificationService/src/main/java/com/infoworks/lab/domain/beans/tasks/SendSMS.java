package com.infoworks.lab.domain.beans.tasks;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.domain.models.Sms;
import com.infoworks.lab.rest.models.Response;

public class SendSMS extends AbstractTask<Sms, Response> {

    public SendSMS() {}

    public SendSMS(Sms message) {
        super(message);
    }

    @Override
    public Response execute(Sms sms) throws RuntimeException {
        if (sms == null){
            sms = getMessage();
        }
        return new Response().setStatus(200).setMessage(sms.toString());
    }

    @Override
    public Response abort(Sms sms) throws RuntimeException {
        return new Response().setStatus(500).setError("Exception In SendSms");
    }
}
