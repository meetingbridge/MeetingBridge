package com.android.meetingbridge;

import java.util.concurrent.atomic.AtomicInteger;

class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);

    static int getID() {
        return c.incrementAndGet();
    }
}
