package com.example.CapstonProject0.Entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LoanTypeConverter implements AttributeConverter<LoanType, String> {

    @Override
    public String convertToDatabaseColumn(LoanType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public LoanType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LoanType.fromCode(dbData);
    }
}
