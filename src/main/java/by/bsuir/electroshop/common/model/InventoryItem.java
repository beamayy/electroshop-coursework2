package by.bsuir.electroshop.common.model;

public class InventoryItem extends BaseEntity {
    private long categoryId;
    private String categoryName;
    private String brand;
    private String model;
    private String technicalSpec;
    private double retailPrice;
    private int stockBalance;
    private int warrantyMonths;

    public InventoryItem() {
    }

    public InventoryItem(long id, long categoryId, String categoryName, String brand, String model,
                         String technicalSpec, double retailPrice, int stockBalance, int warrantyMonths) {
        super(id);
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.brand = brand;
        this.model = model;
        this.technicalSpec = technicalSpec;
        this.retailPrice = retailPrice;
        this.stockBalance = stockBalance;
        this.warrantyMonths = warrantyMonths;
    }

    public InventoryItem(long categoryId, String brand, String model, double retailPrice) {
        this(0, categoryId, null, brand, model, "", retailPrice, 0, 12);
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getTechnicalSpec() {
        return technicalSpec;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public int getStockBalance() {
        return stockBalance;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public void setStockBalance(int stockBalance) {
        this.stockBalance = stockBalance;
    }

    @Override
    public String toString() {
        return brand + " " + model + " [" + stockBalance + " шт.]";
    }
}
