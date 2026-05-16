package by.bsuir.electroshop.common.model;

public class ProductCategory extends BaseEntity {
    private String name;
    private String description;

    public ProductCategory() {
    }

    public ProductCategory(long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}
