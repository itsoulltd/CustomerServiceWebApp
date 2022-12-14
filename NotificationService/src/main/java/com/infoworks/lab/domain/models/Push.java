package com.infoworks.lab.domain.models;

import com.infoworks.lab.rest.models.Message;

public class Push extends Message {

    private String deviceUUID;

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
    }
}
