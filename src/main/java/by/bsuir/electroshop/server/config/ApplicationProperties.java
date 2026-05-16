package by.bsuir.electroshop.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ApplicationProperties {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = ApplicationProperties.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("Файл application.properties не найден");
            }
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка загрузки настроек", e);
        }
    }

    private ApplicationProperties() {
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
