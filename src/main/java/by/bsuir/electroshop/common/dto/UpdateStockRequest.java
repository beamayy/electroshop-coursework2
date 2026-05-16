package by.bsuir.electroshop.common.dto;

import java.io.Serializable;

public record UpdateStockRequest(long productId, int delta) implements Serializable {}
