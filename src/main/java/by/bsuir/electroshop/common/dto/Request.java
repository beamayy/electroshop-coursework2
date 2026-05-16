package by.bsuir.electroshop.common.dto;

import by.bsuir.electroshop.common.enums.CommandType;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final CommandType commandType;
    private final String sessionUser;
    private final Serializable payload;

    public Request(CommandType commandType, String sessionUser, Serializable payload) {
        this.commandType = commandType;
        this.sessionUser = sessionUser;
        this.payload = payload;
    }

    public static Request of(CommandType commandType, Serializable payload) {
        return new Request(commandType, null, payload);
    }

    public static Request of(CommandType commandType, String sessionUser, Serializable payload) {
        return new Request(commandType, sessionUser, payload);
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getSessionUser() {
        return sessionUser;
    }

    public Serializable getPayload() {
        return payload;
    }
}
