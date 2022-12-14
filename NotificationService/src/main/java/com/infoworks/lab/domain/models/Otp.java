package com.infoworks.lab.domain.models;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Otp extends Response {

    private final String value;
    private final Long ttl; //always in milli-seconds
    private final Long timestamp = Instant.now().toEpochMilli();

    public Otp() {this("0000");}

    public Otp(String value) {
        this(value, Duration.ofMinutes(5));
    }

    public Otp(String value, Duration duration) {
        this.value = value;
        this.ttl = duration.toMillis();
    }

    public String getValue() {
        return value;
    }

    public Long getTtl() {
        return ttl;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    private long getExpireDuration(long ttl){
        Calendar calc = Calendar.getInstance();
        calc.setTime(new Date(getTimestamp()));
        calc.add(Calendar.MILLISECOND, Long.valueOf(ttl).intValue());
        return calc.getTimeInMillis();
    }

    public boolean hasExpired() {
        Date expireDate = new Date(getExpireDuration(getTtl()));
        return expireDate.before(new Date());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(marshallingToMap(true));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Object data = in.readObject();
        if (data instanceof Map){
            unmarshallingFromMap((Map) data, true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Otp otp = (Otp) o;
        return value.equalsIgnoreCase(otp.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public boolean isEmpty(){
        return getValue().toString().isEmpty() && getValue().toString().equalsIgnoreCase("0000");
    }

    @Override
    public String toString() {
        try {
            return Message.marshal(marshallingToMap(true));
        } catch (IOException e) {}
        return super.toString();
    }
}
