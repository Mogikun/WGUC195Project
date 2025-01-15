package com.scheduler.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for handling time zone conversions and business hours validation
 * @author [Your Name]
 */
public class TimeUtils {
    private static final ZoneId EST_ZONE = ZoneId.of("America/New_York");
    private static final ZoneId LOCAL_ZONE = ZoneId.systemDefault();

    // Business hours in EST (8:00 AM - 10:00 PM)
    private static final LocalTime BUSINESS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(22, 0);

    /**
     * Converts local time to EST
     * @param localDateTime The local date time to convert
     * @return ZonedDateTime in EST
     */
    public static ZonedDateTime localToEST(LocalDateTime localDateTime) {
        return localDateTime
                .atZone(LOCAL_ZONE)
                .withZoneSameInstant(EST_ZONE);
    }

    /**
     * Converts EST to local time
     * @param estDateTime The EST date time to convert
     * @return LocalDateTime in system default time zone
     */
    public static LocalDateTime estToLocal(ZonedDateTime estDateTime) {
        return estDateTime
                .withZoneSameInstant(LOCAL_ZONE)
                .toLocalDateTime();
    }

    /**
     * Checks if the given time range falls within business hours (EST)
     * @param startLocal Start time in local time zone
     * @param endLocal End time in local time zone
     * @return true if within business hours, false otherwise
     */
    public static boolean isWithinBusinessHours(LocalDateTime startLocal, LocalDateTime endLocal) {
        // Convert to EST
        ZonedDateTime startEST = localToEST(startLocal);
        ZonedDateTime endEST = localToEST(endLocal);

        // Extract time components
        LocalTime startTime = startEST.toLocalTime();
        LocalTime endTime = endEST.toLocalTime();

        // Check if weekend
        DayOfWeek startDay = startEST.getDayOfWeek();
        DayOfWeek endDay = endEST.getDayOfWeek();

        if (startDay.getValue() > 5 || endDay.getValue() > 5) {
            return false; // Weekend
        }

        // Check time bounds
        return !startTime.isBefore(BUSINESS_START) &&
                !endTime.isAfter(BUSINESS_END) &&
                !endTime.isBefore(startTime);
    }

    /**
     * Gets the next available appointment time during business hours
     * @param fromLocal Starting point in local time
     * @return Next available time slot in local time zone
     */
    public static LocalDateTime getNextAvailableTime(LocalDateTime fromLocal) {
        LocalDateTime nextTime = fromLocal;

        while (!isWithinBusinessHours(nextTime, nextTime.plusMinutes(30))) {
            if (nextTime.getHour() >= 22) {
                // If after business hours, move to next day at start of business
                nextTime = nextTime.plusDays(1).withHour(8).withMinute(0);
            } else {
                // Move forward by 30 minutes
                nextTime = nextTime.plusMinutes(30);
            }

            // Skip weekends
            while (nextTime.getDayOfWeek().getValue() > 5) {
                nextTime = nextTime.plusDays(1).withHour(8).withMinute(0);
            }
        }

        return nextTime;
    }

    /**
     * Formats a LocalDateTime for display in the user's timezone
     * @param dateTime The date time to format
     * @return Formatted string representation
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"));
    }

    /**
     * Gets the current time zone ID as a string
     * @return Current time zone ID
     */
    public static String getCurrentTimeZone() {
        return LOCAL_ZONE.getId();
    }

    /**
     * Checks if two time ranges overlap
     * @param start1 Start of first range
     * @param end1 End of first range
     * @param start2 Start of second range
     * @param end2 End of second range
     * @return true if ranges overlap, false otherwise
     */
    public static boolean isOverlapping(LocalDateTime start1, LocalDateTime end1,
                                        LocalDateTime start2, LocalDateTime end2) {
        return !start1.isAfter(end2) && !end1.isBefore(start2);
    }
}