package com.jess.arms.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;

import java.util.List;
import java.util.Map;

/**
 * @author weijianxing
 *         <p/>
 *         封装JSON的操作
 *         <p/>
 *         优点:如果想更换JSON的库的话,只需要修改这个类就可以了
 */
public class FastJsonUtils {

    public static <T> T stringToObject(String jsonString, Class<T> clazz) {
        return JSON.parseObject(jsonString, clazz);
    }

    public static <T> List<T> stringToArrayList(String jsonString, Class<T> clazz) {
        return JSONArray.parseArray(jsonString, clazz);
    }

    public static JSONObject stringToObject(String jsonString) {
        return JSONArray.parseObject(jsonString);
    }

    public static String toJSONString(Object object) {
        return JSON.toJSONString(object);
    }

    public static <K, V> Map<K, V> parseToMap(String json,
                                              Class<K> keyType,
                                              Class<V> valueType) {
        return JSON.parseObject(json,
                new TypeReference<Map<K, V>>(keyType, valueType) {
                }, Feature.OrderedField);
    }
}
