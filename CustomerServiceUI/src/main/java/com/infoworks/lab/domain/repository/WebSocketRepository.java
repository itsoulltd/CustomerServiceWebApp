package com.infoworks.lab.domain.repository;

import com.infoworks.lab.client.spring.SocketTemplate;
import com.infoworks.lab.client.spring.SocketType;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketRepository {

    private static Logger LOG = Logger.getLogger(WebSocketRepository.class.getSimpleName());

    private SocketTemplate socket;
    protected Optional<Object> schema = getEnvProperty("schema", String.class);
    protected Optional<Object> hostName = getEnvProperty("hostName", String.class);
    protected Optional<Object> hostPort = getEnvProperty("port", Integer.class);

    public String getSchema() {
        return schema.isPresent() ? schema.get().toString() : "ws://";
    }
    public String getHostName() {
        return hostName.isPresent() ? hostName.get().toString() : "localhost";
    }
    public String getHostPort() {
        return (hostPort.isPresent()? hostPort.get().toString() : "8080");
    }

    public SocketTemplate getSocket() {
        return socket;
    }

    public void init(boolean enableHeartBeat, String token, String username, String password) throws ExecutionException, InterruptedException {
        socket = new SocketTemplate(SocketType.Standard);
        if (enableHeartBeat) socket.enableHeartbeat(new long[]{10000L, 10000L});
        if (Objects.nonNull(token)) socket.setAuthorizationHeader(token);
        if (Objects.nonNull(username)) socket.setQueryParam("username", username);
        if (Objects.nonNull(password)) socket.setQueryParam("password", password);
        //Handle-Connection Error:
        socket.connectionErrorHandler((throwable) -> {
            LOG.info("WebSocket Connection Failed!");
            LOG.log(Level.WARNING, throwable.getMessage(), throwable);
        });
        //Handle-Connection Success:
        socket.connectionAcceptedHandler((var1, var2) -> {
            //Take other-measures:
            LOG.info("WebSocket Connection Successful!");
        });
        //Connect...
        socket.connect(String.format("%s%s:%s", getSchema(), getHostName(), getHostPort()));
    }

    public void init() throws ExecutionException, InterruptedException {
        init(false, null, null, null);
    }

    public void init(boolean enableHeartBeat) throws ExecutionException, InterruptedException {
        init(enableHeartBeat, null, null, null);
    }

    public void init(boolean enableHeartBeat, String token) throws ExecutionException, InterruptedException {
        init(enableHeartBeat, token, null, null);
    }

    public void disconnect() {
        if (socket != null){
            socket.disconnect();
        }
    }

    public boolean isConnected() {
        return (socket != null) ? socket.isConnected() : false;
    }

    public static Optional<Object> getEnvProperty(String property, Class type) {
        if (System.getenv(property) == null) {
            return Optional.ofNullable(null);
        } else if (type.isAssignableFrom(String.class)) {
            return Optional.ofNullable(System.getenv(property));
        } else {
            if (type.isAssignableFrom(Integer.class)) {
                try {
                    Integer value = Integer.valueOf(System.getenv(property));
                    return Optional.ofNullable(value);
                } catch (NumberFormatException var4) {
                    LOG.log(Level.WARNING, var4.getMessage(), var4);
                }
            } else if (type.isAssignableFrom(Long.class)) {
                try {
                    Long value = Long.valueOf(System.getenv(property));
                    return Optional.ofNullable(value);
                } catch (NumberFormatException var3) {
                    LOG.log(Level.WARNING, var3.getMessage(), var3);
                }
            }

            return Optional.ofNullable(null);
        }
    }

}
