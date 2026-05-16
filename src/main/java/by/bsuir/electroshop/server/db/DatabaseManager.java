package by.bsuir.electroshop.server.db;

import by.bsuir.electroshop.server.config.ApplicationProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public final class DatabaseManager {
    private static DatabaseManager instance;
    private final String jdbcUrl;

    private DatabaseManager() {
        this.jdbcUrl = ApplicationProperties.get("database.url");
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(loadSchema());
            seedData(connection);
        } catch (SQLException e) {
            throw new IllegalStateException("Не удалось инициализировать БД", e);
        }
    }

    private String loadSchema() {
        try (InputStream inputStream = DatabaseManager.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (inputStream == null) {
                throw new IllegalStateException("Файл schema.sql не найден");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка чтения schema.sql", e);
        }
    }

    private void seedData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT OR IGNORE INTO user_roles(name) VALUES ('ADMIN'), ('MANAGER'), ('SELLER')");
            statement.executeUpdate("""
                    INSERT OR IGNORE INTO accounts(id, username, password_hash, status, role_id) VALUES
                    (1, 'admin', 'f865b53623b121fd34ee5426c792e5c33af8c227', 'ACTIVE', 1),
                    (2, 'manager', 'f35bc30c0ab883785eb8909fe8db729e6e591a9e', 'ACTIVE', 2),
                    (3, 'seller', '9aa3e0a62c1eae5bb5784c8ebc2730601285d318', 'ACTIVE', 3)
                    """);
            statement.executeUpdate("""
                    INSERT OR IGNORE INTO staff_profiles(account_id, first_name, last_name, phone) VALUES
                    (1, 'Системный', 'Администратор', '+375291111111'),
                    (2, 'Иван', 'Менеджер', '+375292222222'),
                    (3, 'Павел', 'Продавец', '+375293333333')
                    """);
            statement.executeUpdate("""
                    INSERT OR IGNORE INTO product_categories(id, name, description) VALUES
                    (1, 'Смартфоны', 'Мобильные устройства'),
                    (2, 'Освещение', 'Светильники и лампы'),
                    (3, 'Инструменты', 'Электроинструменты')
                    """);
            statement.executeUpdate("""
                    INSERT OR IGNORE INTO hardware_inventories(id, category_id, brand, model, technical_spec, retail_price, stock_balance, warranty_months) VALUES
                    (1, 1, 'Samsung', 'Galaxy A55', '8/256GB, AMOLED', 1499.99, 12, 24),
                    (2, 2, 'Xiaomi', 'Smart Lamp 2', 'LED, Wi-Fi', 199.50, 20, 12),
                    (3, 3, 'Bosch', 'GBH 2-26', '800W, SDS+', 899.00, 7, 24)
                    """);
        }
    }
}
