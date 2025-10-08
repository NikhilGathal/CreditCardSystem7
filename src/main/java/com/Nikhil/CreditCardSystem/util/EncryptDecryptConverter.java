//package com.Nikhil.CreditCardSystem.util;
//
//
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
////import com.Nikhil.CreditCardSystem.util.AESUtil;
//
//@Converter
//public class EncryptDecryptConverter implements AttributeConverter<String, String> {
//
//    @Override
//    public String convertToDatabaseColumn(String attribute) {
//        return AESUtil.encrypt(attribute);
//    }
//
//    @Override
//    public String convertToEntityAttribute(String dbData) {
//        return AESUtil.decrypt(dbData);
//    }
//}
//
