package com.powerpuff.demo.security.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    int code;
    String Message;
    Map<String, Object> data = new HashMap<String, Object>();

    public static Message denyAccess(String message) {
        Message result = new Message();
        result.setCode(300);
        result.setMessage(message);
        return result;
    }

    public static Message success(String message) {
        Message result = new Message();
        result.setCode(0);
        result.setMessage(message);
        return result;
    }

    public static Message fail(String message) {
        Message result = new Message();
        result.setCode(1);
        result.setMessage(message);
        return result;
    }

    public Message add(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
