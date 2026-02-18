package com.example.fbk_balkan.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate date) {
        return date == null ? null : String.valueOf(date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {

            return LocalDate.parse(dbData);
        } catch (DateTimeParseException e) {

            try {
                long millis = Long.parseLong(dbData);
                return Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Cannot convert db value to LocalDate: " + dbData, ex);
            }
        }
    }
}
