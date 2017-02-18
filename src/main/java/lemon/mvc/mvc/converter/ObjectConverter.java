package lemon.mvc.mvc.converter;

import lemon.mvc.mvc.json.JacksonSupportJson;

public class ObjectConverter implements Converter<Object> {

    @Override
    public Object convert(String s) {
        return JacksonSupportJson.buildNonNullBinder().fromJson(s, Object.class);
    }

}
