package by.bsuir.electroshop.common.model;

public class TransactionItem extends BaseEntity {
    private long inventoryId;
    private String productName;
    private int quantity;
    private double unitPrice;

    public TransactionItem() {
    }

    public TransactionItem(long inventoryId, String productName, int quantity, double unitPrice) {
        this.inventoryId = inventoryId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public long getInventoryId() {
        return inventoryId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getLineTotal() {
        return quantity * unitPrice;
    }
}
