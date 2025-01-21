package com.example.registro.data.repository;

import com.example.registro.data.model.User;
import com.example.registro.data.remote.SocketClient;

public class UserRepository {
    private final SocketClient socketClient;
    private static final int CONNECTION_TIMEOUT = 5000; // 5 seconds

    public interface RegistrationCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public UserRepository(String serverIp, int serverPort) {
        this.socketClient = new SocketClient(serverIp, serverPort);
    }

    public void register(User user, RegistrationCallback callback) {
        // First ensure we're connected
        if (!socketClient.isConnected()) {
            socketClient.connect(new SocketClient.SocketCallback() {
                @Override
                public void onResponse(String response) {
                    // Once connected, send the registration data
                    sendRegistrationData(user, callback);
                }

                @Override
                public void onError(String error) {
                    callback.onError("Failed to connect: " + error);
                }
            });
        } else {
            sendRegistrationData(user, callback);
        }
    }

    private void sendRegistrationData(User user, RegistrationCallback callback) {
        String userData = user.toJson();
        socketClient.sendMessage(userData, new SocketClient.SocketCallback() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void disconnect() {
        socketClient.disconnect();
    }
}