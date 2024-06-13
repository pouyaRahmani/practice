package com.example.practice;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Date implements Serializable {
    private LocalDate date;

    public Date(int day, int month, int year) {
        this.date = LocalDate.of(year, month, day);
    }

    public Date(LocalDate date) {
        this.date = date;
    }

    // Converts the Date object to a string in the format "dd/MM/yyyy"
    public String dateToString() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // Parses a date string in the format "yyyy-MM-dd" and returns a Date object
    public static Date valueOf(String dateStr) {
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return new Date(localDate);
    }

    // Converts the Date object to LocalDate
    public LocalDate toLocalDate() {
        return this.date;
    }

    // Getters and setters for day, month, and year
    public int getDay() {
        return date.getDayOfMonth();
    }

    public void setDay(int day) {
        this.date = this.date.withDayOfMonth(day);
    }

    public int getMonth() {
        return date.getMonthValue();
    }

    public void setMonth(int month) {
        this.date = this.date.withMonth(month);
    }

    public int getYear() {
        return date.getYear();
    }

    public void setYear(int year) {
        this.date = this.date.withYear(year);
    }

    // Compares two Date objects
    public int compareTo(Date other) {
        return this.date.compareTo(other.toLocalDate());
    }

    @Override
    public String toString() {
        return dateToString();
    }
}
