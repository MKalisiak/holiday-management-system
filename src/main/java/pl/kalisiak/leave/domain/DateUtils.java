package pl.kalisiak.leave.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtils {

    public static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }
    
    public static LocalDate laterDate(LocalDate first, LocalDate second) {
        if (first.isAfter(second))
            return first;
        else
            return second;
    }

    public static LocalDate earlierDate(LocalDate first, LocalDate second) {
        if (first.isBefore(second))
            return first;
        else
            return second;
    }

}