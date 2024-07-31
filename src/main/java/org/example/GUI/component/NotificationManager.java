package org.example.GUI.component;

import raven.toast.Notifications;

import java.time.Duration;
import java.time.Instant;

public class NotificationManager {
    private static final long COOLDOWN_MILLIS = 5000; // Cooldown period in milliseconds
    private static Instant lastNotificationTime = Instant.EPOCH;

    public enum NotificationType {
        WARNING,
        SUCCESS,
        INFO,
        ERROR
    }

    private static Instant lastNotificationTimeForType = Instant.EPOCH;

    public static void showNotification(NotificationType type, String message) {
        Instant now = Instant.now();

        // Check cooldown for the given type
        if (Duration.between(lastNotificationTimeForType, now).toMillis() >= COOLDOWN_MILLIS) {
            switch (type) {
                case WARNING:
                    Notifications.getInstance().show(Notifications.Type.WARNING, message);
                    break;
                case SUCCESS:
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, message);
                    break;
                case INFO:
                    Notifications.getInstance().show(Notifications.Type.INFO, message);
                    break;
                case ERROR:
                    Notifications.getInstance().show(Notifications.Type.ERROR, message);
                    break;
            }
            lastNotificationTimeForType = now;
        }
    }

}

