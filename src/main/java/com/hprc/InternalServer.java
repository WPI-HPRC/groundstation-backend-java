package com.hprc;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class InternalServer extends WebSocketServer {

    private final Logger logger = LoggerFactory.getLogger("Telemetry Server");
    public boolean isRunning;

    public InternalServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info(webSocket.getRemoteSocketAddress().getHostString() + " connected to the server!");
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        logger.info(webSocket.getRemoteSocketAddress().getHostString() + " disconnected from the server!");
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        logger.info(webSocket.getRemoteSocketAddress().getHostString() + ": " + s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        logger.error(e.toString());
    }

    @Override
    public void onStart() {
        broadcast("Welcome to the server!");
        this.isRunning = true;
    }

    @Override
    public void stop() throws InterruptedException {
        super.stop();
        this.isRunning = false;
    }
}
