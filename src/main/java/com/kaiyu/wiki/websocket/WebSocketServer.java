package com.kaiyu.wiki.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * Each client has a unique token.
     */
    private String token = "";

    private static HashMap<String, Session> map = new HashMap<>();

    /**
     * connect success
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        map.put(token, session);
        this.token = token;
        LOG.info("New connection: token: {}, session id: {}, current number of connections: {}", token, session.getId(), map.size());
    }

    /**
     * Connection closed
     */
    @OnClose
    public void onClose(Session session) {
        map.remove(this.token);
        LOG.info("Connection closed, token: {}, session id: {}! Current connections: {}", this.token, session.getId(), map.size());
    }

    /**
     * receive message
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.info("Received message: {}, content: {}", token, message);
    }

    /**
     * Connection error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        LOG.error("Error occurred", error);
    }

    /**
     * Group messaging
     */
    public void sendInfo(String message) {
        for (String token : map.keySet()) {
            Session session = map.get(token);
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOG.error("Failed to push message: {}, Content: {}", token, message);
            }
            LOG.info("Push message: {}, Content: {}", token, message);
        }
    }

}
