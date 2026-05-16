package by.bsuir.electroshop.server.repository;

import by.bsuir.electroshop.common.enums.AccountStatus;
import by.bsuir.electroshop.common.enums.Role;
import by.bsuir.electroshop.common.model.Account;
import by.bsuir.electroshop.server.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class AccountRepository {
    public Optional<Account> findByUsername(String username) throws SQLException {
        String sql = """
                SELECT a.id, a.username, a.password_hash, a.status, r.name AS role_name
                FROM accounts a JOIN user_roles r ON r.id = a.role_id
                WHERE a.username = ?
                """;
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAccount(rs));
                }
            }
        }
        return Optional.empty();
    }

    public long createUser(String username, String passwordHash, Role role) throws SQLException {
        String sql = "INSERT INTO accounts(username, password_hash, status, role_id) VALUES (?, ?, 'ACTIVE', (SELECT id FROM user_roles WHERE name = ?))";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, role.name());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                return rs.next() ? rs.getLong(1) : -1;
            }
        }
    }

    public void createProfile(long accountId, String firstName, String lastName, String phone) throws SQLException {
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO staff_profiles(account_id, first_name, last_name, phone) VALUES (?, ?, ?, ?)")) {
            statement.setLong(1, accountId);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, phone);
            statement.executeUpdate();
        }
    }

    public void blockUser(String username) throws SQLException {
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE accounts SET status = 'BLOCKED' WHERE username = ?")) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                AccountStatus.valueOf(rs.getString("status")),
                Role.valueOf(rs.getString("role_name"))
        );
    }
}
