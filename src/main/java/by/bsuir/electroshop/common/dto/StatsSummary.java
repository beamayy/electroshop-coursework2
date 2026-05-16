package by.bsuir.electroshop.common.dto;

import java.io.Serializable;

public record StatsSummary(long productsCount, long categoriesCount, long salesCount,
                           double revenue, long lowStockCount) implements Serializable {}
