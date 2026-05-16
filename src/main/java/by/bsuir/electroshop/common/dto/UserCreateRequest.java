package by.bsuir.electroshop.common.dto;

import by.bsuir.electroshop.common.enums.Role;

import java.io.Serializable;

public record UserCreateRequest(String username, String password, String firstName, String lastName,
                                String phone, Role role) implements Serializable {}
