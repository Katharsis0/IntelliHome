package com.example.registro.data.service;

import com.example.registro.data.model.Message;
import com.example.registro.data.model.User;
import com.example.registro.data.remote.SocketClient;

public class AuthService {
    private final SocketClient socketClient;

    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public AuthService(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    public void login(String username, String password, AuthCallback callback) {
        Message loginMessage = Message.createLoginMessage(username, password);
        sendAuthMessage(loginMessage, callback);
    }

    public void register(User user, AuthCallback callback) {
        Message registrationMessage = Message.createRegistrationMessage(user);
        if (registrationMessage != null) {
            sendAuthMessage(registrationMessage, callback);
        } else {
            callback.onError("Error creating registration message");
        }
    }

    private void sendAuthMessage(Message message, AuthCallback callback) {
        socketClient.sendMessage(message.toJson(), new SocketClient.SocketCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    Message responseMsg = new Message(response);
                    if (responseMsg.getStatus().equals("success")) {
                        callback.onSuccess(responseMsg.getMessage());
                    } else {
                        callback.onError(responseMsg.getMessage());
                    }
                } catch (Exception e) {
                    callback.onError("Error processing response: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError("Network error: " + error);
            }
        });
    }
}