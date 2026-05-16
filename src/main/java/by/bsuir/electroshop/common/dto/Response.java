package by.bsuir.electroshop.common.dto;

import java.io.Serial;
import java.io.Serializable;

public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final Serializable data;

    public Response(boolean success, String message, Serializable data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static Response ok(String message, Serializable data) {
        return new Response(true, message, data);
    }

    public static Response error(String message) {
        return new Response(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Serializable getData() {
        return data;
    }
}
