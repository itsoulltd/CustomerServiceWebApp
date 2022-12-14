package com.infoworks.lab.domain.beans.tasks.OtpTasks;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.domain.models.Otp;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.sql.query.models.Property;

public class VerifyOtpFromCache extends AbstractTask<Otp, Response> {

    private DataSource<String, Otp> cache;

    public void setCache(DataSource<String, Otp> cache) {
        this.cache = cache;
    }

    public VerifyOtpFromCache() {}

    public VerifyOtpFromCache(String key) {
        super(new Property("key", key));
    }

    public VerifyOtpFromCache(String key, DataSource<String, Otp> cache) {
        this(key);
        setCache(cache);
    }

    public VerifyOtpFromCache(String key, Otp store, DataSource<String, Otp> cache) {
        this(key, store);
        setCache(cache);
    }

    public VerifyOtpFromCache(String key, Otp store) {
        super(new Property("key", key), new Property("otp", store.toString()));
    }

    @Override
    public Response execute(Otp otp) throws RuntimeException {
        if (otp == null){
            String otpJson = getPropertyValue("otp").toString();
            try {
                otp = Message.unmarshal(Otp.class, otpJson);
            } catch (Exception e) {}
        }
        String key = getPropertyValue("key").toString();
        Otp lastSaved = cache.remove(key);
        if (lastSaved == null){
            return new Response().setStatus(404).setError("Otp Not Found!");
        }
        if (!lastSaved.hasExpired()){
            boolean hasMatched = lastSaved.equals(otp);
            if (hasMatched)
                return new Response().setStatus(200).setMessage("Otp Verified.");
            else
                return new Response().setStatus(417).setError("Verification Failed!");
        }
        return new Response().setStatus(408).setError("Otp ttl expired");
    }

    @Override
    public Response abort(Otp otp) throws RuntimeException {
        return new Response().setStatus(500).setError("Exception in VerifyOtpFromCache");
    }
}
