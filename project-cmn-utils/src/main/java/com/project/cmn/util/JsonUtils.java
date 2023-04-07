package com.project.cmn.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.List;

/**
 * Json String to Object 또는 Object to Json String 을 위한 유틸
 */
public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    /**
     * Object 를 JSON String로 변환한다.
     *
     * @param obj 변환할 Object
     * @return String
     * @throws JsonProcessingException Json 변환 오류
     */
    public static String toJsonStr(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

    /**
     * Object 을 JSON String 으로 변환 (pretty format)
     *
     * @param obj JSON String 으로 변환할 객체
     * @return 변환된 JSON String
     * @throws JsonProcessingException Json 변환 오류
     */
    public static String toJsonStrPretty(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * JSON String 을 pretty format 형태의 JSON String 으로 변환
     *
     * @param jsonStr 변환할 JSON String
     * @return pretty format 형태의 JSON String
     * @throws JsonProcessingException Json 변환 오류
     */
    public static String toJsonStrPretty(String jsonStr) throws JsonProcessingException {
        Object obj = OBJECT_MAPPER.readValue(jsonStr, Object.class);

        return toJsonStrPretty(obj);
    }

    /**
     * JSON String 를 Object 로 변환한다.
     *
     * @param <T>   Generic Type
     * @param json  변환할 JSON String
     * @param clazz 변환할 Object class
     * @return Object 변환된 Object
     * @throws JsonProcessingException Json 변환 오류
     */
    public static <T> Object toObject(String json, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    /**
     * JSON String 을 Object 로 변환한다.
     * <pre>
     * {@code
     *     TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
     *     HashMap<String,Object> map = JsonUtils.toObject(json, typeRef);
     * }
     * </pre>
     *
     * @param <T>          Generic Type
     * @param json         변환할 JSON string
     * @param valueTypeRef 변환할 Json Type
     * @return Object 변환된 Object
     * @throws JsonProcessingException Json 변환 오류
     */
    public static <T> Object toObject(String json, TypeReference<T> valueTypeRef) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, valueTypeRef);
    }

    /**
     * JSON String 을 List<Object>로 변환한다.
     *
     * @param <T>   Generic Type
     * @param json  변환할 JSON String
     * @param clazz 변환할 Object class
     * @return 변환된 List<Object>
     * @throws JsonProcessingException Json 변환 오류
     */
    public static <T> List<T> toList(String json, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
    }

    /**
     * {@link Object}를 {@link Class<T>}로 변환한다.
     *
     * @param obj   {@link Object} 소스
     * @param clazz {@link Class} 타켓
     * @param <T>   {@link T}
     * @return 변환된 {@link Class}
     */
    public static <T> Object convert(Object obj, Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(obj, clazz);
    }
}
