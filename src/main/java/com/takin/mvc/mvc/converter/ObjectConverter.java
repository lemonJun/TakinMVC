package com.takin.mvc.mvc.converter;

public class ObjectConverter implements Converter<Object> {

    @Override
    public Object convert(String s) {
        return JacksonSupportJson.buildNonNullBinder().fromJson(s, Object.class);
    }

}
