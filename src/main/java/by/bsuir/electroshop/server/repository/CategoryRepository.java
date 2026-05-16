package by.bsuir.electroshop.server.repository;

import by.bsuir.electroshop.common.model.ProductCategory;
import by.bsuir.electroshop.server.db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    public long create(String name, String description) throws SQLException {
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO product_categories(name, description) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                return rs.next() ? rs.getLong(1) : -1;
            }
        }
    }

    public List<ProductCategory> findAll() throws SQLException {
        List<ProductCategory> result = new ArrayList<>();
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, name, description FROM product_categories ORDER BY id");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(new ProductCategory(rs.getLong("id"), rs.getString("name"), rs.getString("description")));
            }
        }
        return result;
    }
}
