package by.bsuir.electroshop.common.dto;

import java.io.Serializable;

public record UpdatePriceRequest(long productId, double newPrice) implements Serializable {}
