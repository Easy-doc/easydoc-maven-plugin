package com.stalary;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author lirongqian
 * @date 2022/5/20
 **/
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //FAIL_ON_UNKNOWN_PROPERTIES在序列化的时候，如果遇到不认识的字段的处理方式
        //默认启用特性，这意味着在遇到未知属性时抛出JsonMappingException。在引入该特性之前，这是默认的默认设置。
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //在序列化时，只有那些值为null或被认为为空的值的属性才不会被包含在内。
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);
    }
    
    public static <T> String objectToJson(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
