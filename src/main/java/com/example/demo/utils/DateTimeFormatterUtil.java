package com.example.demo.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.*;

public class DateTimeFormatterUtil {
    public static String LocalDateTimeToString(LocalDateTime time) {
        DateTimeFormatter formatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return time.format(formatter);
    }
}
