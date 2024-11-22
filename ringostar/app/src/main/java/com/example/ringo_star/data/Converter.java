package com.example.ringo_star.data;

import androidx.room.TypeConverter;

import com.example.ringo_star.enumeration.BloodGroup;
import com.example.ringo_star.enumeration.Gender;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Converter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @TypeConverter
    public static String fromGender(Gender gender) {
        return gender == null ? null : gender.name();
    }

    @TypeConverter
    public static Gender toGender(String gender) {
        return gender == null ? null : Gender.valueOf(gender);
    }

    @TypeConverter
    public static String fromBloodGroup(BloodGroup bloodGroup) {
        return bloodGroup == null ? null : bloodGroup.name();
    }

    @TypeConverter
    public static BloodGroup toBloodGroup(String bloodGroup) {
        return bloodGroup == null ? null : BloodGroup.valueOf(bloodGroup);
    }

    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.format(formatter);
    }

    @TypeConverter
    public static LocalDate toLocalDate(String date) {
        return date == null ? null : LocalDate.parse(date, formatter);
    }
}