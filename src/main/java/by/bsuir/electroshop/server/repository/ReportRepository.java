package by.bsuir.electroshop.server.repository;

import by.bsuir.electroshop.common.dto.StatsSummary;
import by.bsuir.electroshop.server.db.DatabaseManager;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportRepository {
    public List<String> salesByEmployee() throws SQLException {
        String sql = """
                SELECT a.username, COUNT(t.id) AS checks_count, COALESCE(SUM(t.total_amount), 0) AS revenue
                FROM accounts a LEFT JOIN transactions t ON t.account_id = a.id
                GROUP BY a.username
                ORDER BY revenue DESC
                """;
        return readStrings(sql, rs -> rs.getString("username") + " | чеков: " + rs.getLong("checks_count") + " | выручка: " + rs.getDouble("revenue"));
    }

    public List<String> salesByCategory() throws SQLException {
        String sql = """
                SELECT c.name, COALESCE(SUM(ti.quantity), 0) AS qty, COALESCE(SUM(ti.quantity * ti.unit_price), 0) AS revenue
                FROM product_categories c
                LEFT JOIN hardware_inventories p ON p.category_id = c.id
                LEFT JOIN transaction_items ti ON ti.inventory_id = p.id
                GROUP BY c.name
                ORDER BY revenue DESC
                """;
        return readStrings(sql, rs -> rs.getString("name") + " | шт.: " + rs.getLong("qty") + " | выручка: " + rs.getDouble("revenue"));
    }

    public StatsSummary summary() throws SQLException {
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            return new StatsSummary(
                    count(connection, "SELECT COUNT(*) FROM hardware_inventories"),
                    count(connection, "SELECT COUNT(*) FROM product_categories"),
                    count(connection, "SELECT COUNT(*) FROM transactions"),
                    sum(connection, "SELECT COALESCE(SUM(total_amount), 0) FROM transactions"),
                    count(connection, "SELECT COUNT(*) FROM hardware_inventories WHERE stock_balance < 5")
            );
        }
    }

    private long count(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    private double sum(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    private List<String> readStrings(String sql, SqlMapper mapper) throws SQLException {
        List<String> result = new ArrayList<>();
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(mapper.map(rs));
            }
        }
        return result;
    }

    @FunctionalInterface
    private interface SqlMapper extends Serializable {
        String map(ResultSet rs) throws SQLException;
    }
}
