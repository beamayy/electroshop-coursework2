package by.bsuir.electroshop.server.repository;

import by.bsuir.electroshop.common.model.InventoryItem;
import by.bsuir.electroshop.server.db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {
    public List<InventoryItem> findAll() throws SQLException {
        String sql = baseSelect() + " ORDER BY p.id";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return readItems(rs);
        }
    }

    public List<InventoryItem> search(String keyword) throws SQLException {
        String sql = baseSelect() + " WHERE LOWER(CONCAT(p.brand, ' ', p.model, ' ', c.name, ' ', IFNULL(p.technical_spec, ''))) LIKE ? ORDER BY p.id";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + keyword.toLowerCase() + "%");
            try (ResultSet rs = statement.executeQuery()) {
                return readItems(rs);
            }
        }
    }

    public Optional<InventoryItem> findById(long id, Connection connection) throws SQLException {
        String sql = baseSelect() + " WHERE p.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        }
        return Optional.empty();
    }

    public long create(InventoryItem item) throws SQLException {
        String sql = "INSERT INTO hardware_inventories(category_id, brand, model, technical_spec, retail_price, stock_balance, warranty_months) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, item.getCategoryId());
            statement.setString(2, item.getBrand());
            statement.setString(3, item.getModel());
            statement.setString(4, item.getTechnicalSpec());
            statement.setDouble(5, item.getRetailPrice());
            statement.setInt(6, item.getStockBalance());
            statement.setInt(7, item.getWarrantyMonths());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                return rs.next() ? rs.getLong(1) : -1;
            }
        }
    }

    public void updatePrice(long productId, double newPrice) throws SQLException {
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE hardware_inventories SET retail_price = ? WHERE id = ?")) {
            statement.setDouble(1, newPrice);
            statement.setLong(2, productId);
            statement.executeUpdate();
        }
    }

    public void changeStock(long productId, int delta) throws SQLException {
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            changeStock(connection, productId, delta);
        }
    }

    public void changeStock(Connection connection, long productId, int delta) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE hardware_inventories SET stock_balance = stock_balance + ? WHERE id = ?")) {
            statement.setInt(1, delta);
            statement.setLong(2, productId);
            statement.executeUpdate();
        }
    }

    private String baseSelect() {
        return """
                SELECT p.id, p.category_id, c.name AS category_name, p.brand, p.model,
                       p.technical_spec, p.retail_price, p.stock_balance, p.warranty_months
                FROM hardware_inventories p JOIN product_categories c ON c.id = p.category_id
                """;
    }

    private List<InventoryItem> readItems(ResultSet rs) throws SQLException {
        List<InventoryItem> items = new ArrayList<>();
        while (rs.next()) {
            items.add(map(rs));
        }
        return items;
    }

    private InventoryItem map(ResultSet rs) throws SQLException {
        return new InventoryItem(
                rs.getLong("id"),
                rs.getLong("category_id"),
                rs.getString("category_name"),
                rs.getString("brand"),
                rs.getString("model"),
                rs.getString("technical_spec"),
                rs.getDouble("retail_price"),
                rs.getInt("stock_balance"),
                rs.getInt("warranty_months")
        );
    }
    public void deleteById(int id) {
        String sql = "DELETE FROM hardware_inventories WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления товара", e);
        }
    }
}
