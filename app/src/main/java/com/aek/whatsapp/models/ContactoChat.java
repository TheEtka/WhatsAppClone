package com.aek.whatsapp.models;

import com.aek.whatsapp.utils.TimestampConverter;

public class ContactoChat {

    public boolean isActive, isChatVisto;
    public long timestamp;

    public ContactoChat() {
    }

    public ContactoChat(boolean isChatVisto, long timestamp) {
        this.isActive = true;
        this.isChatVisto = isChatVisto;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ContactoChat{" +
                "isActive=" + isActive +
                ", isChatVisto=" + isChatVisto +
                ", timestamp=" + TimestampConverter.getTimestamp(timestamp) +
                '}';
    }
}
