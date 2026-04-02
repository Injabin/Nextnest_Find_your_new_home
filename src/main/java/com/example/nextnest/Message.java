package com.example.nextnest;

import java.io.Serializable;

public class Message implements Serializable {
    public int senderId;
    public int receiverId;
    public String message;
    public long timestamp;

    public Message(int senderId, int receiverId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "From: " + senderId + ", To: " + receiverId + ", Message: " + message;
    }
}
