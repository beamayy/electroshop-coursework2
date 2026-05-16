package by.bsuir.electroshop.common.dto;

import by.bsuir.electroshop.common.enums.Role;

import java.io.Serializable;

public record LoginResponse(long accountId, String username, Role role) implements Serializable {}
