package by.bsuir.electroshop.server.repository;

import by.bsuir.electroshop.common.model.SaleTransaction;
import by.bsuir.electroshop.common.model.TransactionItem;
import by.bsuir.electroshop.server.db.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SaleRepository {
    public long createSale(Connection connection, long accountId, String customerName, double totalAmount) throws SQLException {
        String sql = "INSERT INTO transactions(account_id, created_at, total_amount, customer_name) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, accountId);
            statement.setString(2, LocalDateTime.now().toString());
            statement.setDouble(3, totalAmount);
            statement.setString(4, customerName);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                return rs.next() ? rs.getLong(1) : -1;
            }
        }
    }

    public void createItem(Connection connection, long transactionId, TransactionItem item) throws SQLException {
        String sql = "INSERT INTO transaction_items(transaction_id, inventory_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, transactionId);
            statement.setLong(2, item.getInventoryId());
            statement.setInt(3, item.getQuantity());
            statement.setDouble(4, item.getUnitPrice());
            statement.executeUpdate();
        }
    }

    public List<SaleTransaction> findSalesByUsername(String username) throws SQLException {
        String sql = joinedSelect() + " WHERE a.username = ? ORDER BY t.id DESC";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                return mapSales(rs);
            }
        }
    }

    public List<SaleTransaction> findAllSales() throws SQLException {
        String sql = joinedSelect() + " ORDER BY t.id DESC";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return mapSales(rs);
        }
    }

    private String joinedSelect() {
        return """
                SELECT t.id AS transaction_id, t.account_id, a.username, t.created_at, t.total_amount, t.customer_name,
                       ti.inventory_id, ti.quantity, ti.unit_price, CONCAT(p.brand, ' ', p.model) AS product_name
                FROM transactions t
                JOIN accounts a ON a.id = t.account_id
                LEFT JOIN transaction_items ti ON ti.transaction_id = t.id
                LEFT JOIN hardware_inventories p ON p.id = ti.inventory_id
                """;
    }

    private List<SaleTransaction> mapSales(ResultSet rs) throws SQLException {
        Map<Long, SaleTransaction> map = new LinkedHashMap<>();
        while (rs.next()) {
            long transactionId = rs.getLong("transaction_id");
            if (!map.containsKey(transactionId)) {
                map.put(transactionId, new SaleTransaction(
                        transactionId,
                        rs.getLong("account_id"),
                        rs.getString("username"),
                        LocalDateTime.parse(rs.getString("created_at").replace(' ', 'T')),
                        rs.getDouble("total_amount"),
                        rs.getString("customer_name")
                ));
            }
            long inventoryId = rs.getLong("inventory_id");
            if (!rs.wasNull()) {
                map.get(transactionId).getItems().add(new TransactionItem(
                        inventoryId,
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                ));
            }
        }
        return new ArrayList<>(map.values());
    }
}