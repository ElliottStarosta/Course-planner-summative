package org.example.gui.manager;

import raven.toast.Notifications;

import java.time.Duration;
import java.time.Instant;

/**
 * Manages the display of notifications with different types and ensures cooldown periods
 * between consecutive notifications of the same type.
 */
public class NotificationManager {
    /**
     * Cooldown period in milliseconds between notifications of the same type.
     */
    private static final long COOLDOWN_MILLIS = 1000; // 1 second cooldown

    /**
     * Tracks the last time a notification of any type was displayed.
     */
    private static Instant lastNotificationTime = Instant.EPOCH;

    /**
     * Enum representing the types of notifications that can be displayed.
     */
    public enum NotificationType {
        /**
         * Represents a warning notification type.
         */
        WARNING,

        /**
         * Represents a success notification type.
         */
        SUCCESS,

        /**
         * Represents an informational notification type.
         */
        INFO,

        /**
         * Represents an error notification type.
         */
        ERROR
    }

    /**
     * Tracks the last time a notification of a specific type was displayed.
     */
    private static Instant lastNotificationTimeForType = Instant.EPOCH;

    /**
     * Displays a notification of the specified type with the given message, ensuring that
     * notifications of the same type are not displayed more frequently than the cooldown period.
     *
     * @param type    The type of the notification to display. Must be one of {@link NotificationType}.
     * @param message The message to be displayed in the notification.
     */
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