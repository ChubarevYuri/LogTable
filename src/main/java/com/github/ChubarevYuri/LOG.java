package com.github.ChubarevYuri;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Send log class.
 */
public class LOG extends BaseLog {

    /**
     * Send message in linked files if {@code level} higher than min level.
     * <br>After send message check filesize.
     *
     * @param label   for linked with file.
     * @param level   log level.
     * @param message text.
     */
    public static void send(@Nullable String label, @NotNull Level level, @Nullable String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        message = message.replace("\\", "\\\\");
        message = message.replace("\n", "\\n");
        message = message.replace("\r", "\\r");
        message = message.replace("\t", "\\t");
        message = message.replace("\b", "\\b");
        message = message.replace("\f", "\\f");
        message = String.format("%s %-7s %s",
                new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS").format(new Date()),
                level,
                message);
        try {
            synchronized (BaseLog.class) {
                if (LOG.level.compareTo(level) > 0) {
                    return;
                }
                BaseLog.send(label, message);
                System.out.println(message);
                sizeControl(label, 0);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Send message in base log file if {@code level} higher than min level.
     * <br>After send message check filesize.
     *
     * @param level   log level.
     * @param message text.
     */
    public static void send(@NotNull Level level, @Nullable String message) {
        send(null, level, message);
    }

    /**
     * Send message in linked files if min level at least {@code TRACE}.
     * <br>After send message check filesize.
     *
     * @param label   for linked with file.
     * @param message text.
     */
    public static void TRACE(@Nullable String label, @Nullable String message) {
        send(label, Level.TRACE, message);
    }

    /**
     * Send message in base log file if min level at least {@code TRACE}.
     * <br>After send message check filesize.
     *
     * @param message text.
     */
    public static void TRACE(@Nullable String message) {
        send(Level.TRACE, message);
    }

    /**
     * Send message in linked files if min level at least {@code DEBUG}.
     * <br>After send message check filesize.
     *
     * @param label   for linked with file.
     * @param message text.
     */
    public static void DEBUG(@Nullable String label, @Nullable String message) {
        send(label, Level.DEBUG, message);
    }

    /**
     * Send message in base log file if min level at least {@code DEBUG}.
     * <br>After send message check filesize.
     *
     * @param message text.
     */
    public static void DEBUG(@Nullable String message) {
        send(Level.DEBUG, message);
    }

    /**
     * Send message in linked files if min level at least {@code INFO}.
     * <br>After send message check filesize.
     *
     * @param label   for linked with file.
     * @param message text.
     */
    public static void INFO(@Nullable String label, @Nullable String message) {
        send(label, Level.INFO, message);
    }

    /**
     * Send message in base log file if min level at least {@code INFO}.
     * <br>After send message check filesize.
     *
     * @param message text.
     */
    public static void INFO(@Nullable String message) {
        send(Level.INFO, message);
    }

    /**
     * Send message in linked files if min level at least {@code SETTING}.
     * <br>After send message check filesize.
     *
     * @param label   for linked with file.
     * @param message text.
     */
    public static void SETTING(@Nullable String label, @Nullable String message) {
        send(label, Level.SETTING, message);
    }

    /**
     * Send message in base log file if min level at least {@code SETTING}.
     * <br>After send message check filesize.
     *
     * @param message text.
     */
    public static void SETTING(@Nullable String message) {
        send(Level.SETTING, message);
    }

    /**
     * Send message in linked files if min level at least {@code WARNING}.
     * <br>After send message check filesize.
     *
     * @param label   for linked with file.
     * @param message text.
     */
    public static void WARNING(@Nullable String label, @Nullable String message) {
        send(label, Level.WARNING, message);
    }

    /**
     * Send message in base log file if min level at least {@code WARNING}.
     * <br>After send message check filesize.
     *
     * @param message text.
     */
    public static void WARNING(@Nullable String message) {
        send(Level.WARNING, message);
    }

    /**
     * Send message in linked files if min level at least {@code ERROR}.
     * <br>After send message check filesize.
     *
     * @param label   for linked with file.
     * @param message text.
     */
    public static void ERROR(@Nullable String label, @Nullable String message) {
        send(label, Level.ERROR, message);
    }

    /**
     * Send message in base log file if min level at least {@code ERROR}.
     * <br>After send message check filesize.
     *
     * @param message text.
     */
    public static void ERROR(@Nullable String message) {
        send(Level.ERROR, message);
    }

    /**
     * Send message in linked files.
     * <br>After send message check filesize.
     *
     * @param label   for linked with file.
     * @param message text.
     */
    public static void FATAL(@Nullable String label, @Nullable String message) {
        send(label, Level.FATAL, message);
    }

    /**
     * Send message in base log file.
     * <br>After send message check filesize.
     *
     * @param message text.
     */
    public static void FATAL(@Nullable String message) {
        send(Level.FATAL, message);
    }
}
