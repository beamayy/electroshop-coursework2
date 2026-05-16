package by.bsuir.electroshop.common.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleTransaction extends BaseEntity {
    private long accountId;
    private String employeeUsername;
    private LocalDateTime createdAt;
    private double totalAmount;
    private String customerName;
    private List<TransactionItem> items = new ArrayList<>();

    public SaleTransaction() {
    }

    public SaleTransaction(long id, long accountId, String employeeUsername, LocalDateTime createdAt,
                           double totalAmount, String customerName) {
        super(id);
        this.accountId = accountId;
        this.employeeUsername = employeeUsername;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.customerName = customerName;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<TransactionItem> getItems() {
        return items;
    }

    public void setItems(List<TransactionItem> items) {
        this.items = items;
    }
}
