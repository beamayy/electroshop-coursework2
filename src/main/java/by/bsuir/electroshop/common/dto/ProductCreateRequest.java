package by.bsuir.electroshop.common.dto;

import java.io.Serializable;

public record ProductCreateRequest(long categoryId, String brand, String model, String technicalSpec,
                                   double retailPrice, int stockBalance, int warrantyMonths) implements Serializable {}
