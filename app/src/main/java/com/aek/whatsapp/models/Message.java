package com.aek.whatsapp.models;

import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.utils.TimestampConverter;

public class Message {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_GIF = 1;
    public static final int TYPE_FOTO = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_AUDIO = 4;
    public static final int TYPE_DOC_PDF = 5;

    private String uidAuthor, uidReceiver, message, dataUrl, giphyMediaId, videoThumbnailUrl;
    private int type;
    private long timestamp;

    public Message() {
    }

    public Message(String uidReceiver, String message, String dataUrl, String giphyMediaId, String videoThumbnailUrl, int type) {
        this.uidAuthor = FbUser.getCurrentUserId();
        this.uidReceiver = uidReceiver;
        this.message = message;
        this.dataUrl = dataUrl;
        this.giphyMediaId = giphyMediaId;
        this.videoThumbnailUrl = videoThumbnailUrl;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUidAuthor() {
        return uidAuthor;
    }

    public String getUidReceiver() {
        return uidReceiver;
    }

    public String getMessage() {
        return message;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public String getGiphyMediaId() {
        return giphyMediaId;
    }

    public String getVideoThumbnailUrl() {
        return videoThumbnailUrl;
    }

    public int getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + getMessage() + '\'' +
                ", timestamp=" + TimestampConverter.getTimestamp(getTimestamp()) +
                '}';
    }
}
