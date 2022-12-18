package com.infoworks.lab.domain.repository;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class ChatBoxRepository extends WebSocketRepository{

    private static Logger LOG = Logger.getLogger(WebSocketRepository.class.getSimpleName());

    @Override
    public void init(String appName) throws ExecutionException, InterruptedException {
        super.init(appName);
    }
}
