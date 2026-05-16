package by.bsuir.electroshop.common.dto;

import java.io.Serializable;

public record CategoryCreateRequest(String name, String description) implements Serializable {}
