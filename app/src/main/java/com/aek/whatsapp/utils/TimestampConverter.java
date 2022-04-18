package com.aek.whatsapp.utils;

import android.text.format.DateUtils;

public class TimestampConverter {

    public static String getTimestamp(long timestamp) {
        String timestampRaw = DateUtils.getRelativeTimeSpanString(timestamp).toString();
        return timestampRaw.toLowerCase();
    }

}
