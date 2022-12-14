package com.infoworks.lab.domain.beans.tasks.OtpTasks;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.domain.models.Otp;
import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.sql.query.models.Property;

public class StoreOtpInCache extends AbstractTask<Otp, Otp> {

    private DataSource<String, Otp> cache;

    public void setCache(DataSource<String, Otp> cache) {
        this.cache = cache;
    }

    public StoreOtpInCache() {}

    public StoreOtpInCache(String key) {
        super(new Property("key", key));
    }

    public StoreOtpInCache(String key, DataSource<String, Otp> cache) {
        this(key);
        setCache(cache);
    }

    public StoreOtpInCache(String key, Otp store, DataSource<String, Otp> cache) {
        this(key, store);
        setCache(cache);
    }

    public StoreOtpInCache(String key, Otp store) {
        super(new Property("key", key), new Property("otp", store.toString()));
    }

    @Override
    public Otp execute(Otp otp) throws RuntimeException {
        if (otp == null){
            String otpJson = getPropertyValue("otp").toString();
            try {
                otp = Message.unmarshal(Otp.class, otpJson);
            } catch (Exception e) {}
        }
        String key = getPropertyValue("key").toString();
        cache.put(key, otp);
        return otp;
    }

    @Override
    public Otp abort(Otp otp) throws RuntimeException {
        return (Otp) new Otp().setStatus(500).setError("Exception in StoreOtpInCache");
    }
}
