package com.example.fbk_balkan.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

//@Converter(autoApply = true)
//public class LocalDateConverter
//        implements AttributeConverter<LocalDate, String> {
//
//    @Override
//    public String convertToDatabaseColumn(LocalDate date) {
//        return date == null ? null : date.toString();
//    }
//
//    @Override
//    public LocalDate convertToEntityAttribute(String value) {
//        return value == null ? null : LocalDate.parse(value);
//    }
//}

//@Converter(autoApply = true)
//public class LocalDateConverter implements AttributeConverter<LocalDate, String> {
//
//    @Override
//    public String convertToDatabaseColumn(LocalDate date) {
//        return date == null ? null : String.valueOf(date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
//    }
//
//    @Override
//    public LocalDate convertToEntityAttribute(String dbData) {
//        if (dbData == null) {
//            return null;
//        }
//        try {
//            // إذا كان ISO date
//            return LocalDate.parse(dbData);
//        } catch (DateTimeParseException e) {
//            // إذا كان timestamp عددي (الحالة القديمة)
//            try {
//                long millis = Long.parseLong(dbData);
//                return Instant.ofEpochMilli(millis)
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDate();
//            } catch (NumberFormatException ex) {
//                throw new IllegalArgumentException("Cannot convert db value to LocalDate: " + dbData, ex);
//            }
//        }
//    }
//}
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDateTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        // Numeric epoch ms?
        if (dbData.matches("\\d+")) {
            long millis = Long.parseLong(dbData);
            return Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        // Otherwise parse ISO date/time with optional fractional seconds
        return LocalDateTime.parse(dbData, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
