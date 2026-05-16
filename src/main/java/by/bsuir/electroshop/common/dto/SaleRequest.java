package by.bsuir.electroshop.common.dto;

import by.bsuir.electroshop.common.model.TransactionItem;

import java.io.Serializable;
import java.util.List;

public record SaleRequest(String employeeUsername, String customerName, List<TransactionItem> items) implements Serializable {}
