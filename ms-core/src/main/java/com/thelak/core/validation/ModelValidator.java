package com.thelak.core.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
public class ModelValidator implements org.springframework.validation.Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object obj, Errors errors) {
        /*try {
//            System.out.println("\n\nmodel: " + obj);
            Field[] fields = obj.getClass().getDeclaredFields();


            for (int i = 0; i < fields.length; i++) {

                Field field = obj.getClass().getDeclaredField(fields[i].getName());
                field.setAccessible(true);
                Object value = field.get(obj);
                Column column = fields[i].getDeclaredAnnotation(Column.class);
                if (field.getName().equals("fullName")) {
                    Field field1 = obj.getClass().getDeclaredField("organizationTypeId");
                    field1.setAccessible(true);
                    if (field1.get(obj).equals(2) && value == null)
                    errors.reject("Значение поля не может быть пустым для Юр. лица - " + field);
                }
                if (column != null) {
                    if ((!column.nullable()) && value == null) {
                        errors.reject("Значение поля не может быть пустым - " + field);
                    } else if (value != null && field.getType() == String.class && value.toString().length() > column.length())
                        errors.reject("Превышена допустимая длина поля - " + field);
                }
                if (field.getType() == UUID.class && value != null) {
                    try {
                        UUID.fromString(value.toString());
                    } catch (Exception e) {
                        errors.reject("Значение не соответсвует формату UUID - " + field);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
