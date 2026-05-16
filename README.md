# ElectroShop Coursework

Курсовой проект по дисциплине «Программирование сетевых приложений».

## Что реализовано
- архитектура client-server;
- многопоточный сервер на `ServerSocket` + `ExecutorService`;
- бизнес-логика только на сервере;
- JDBC + SQLite;
- роли: `ADMIN`, `MANAGER`, `SELLER`;
- сериализация запросов и ответов через `ObjectInputStream/ObjectOutputStream`;
- паттерны: `Singleton` (`DatabaseManager`) и `Factory Method` (`RequestHandlerFactory`);
- более 12 высокоуровневых вариантов использования.

## Команды
1. LOGIN
2. LIST_PRODUCTS
3. SEARCH_PRODUCTS
4. CREATE_SALE
5. GET_MY_SALES
6. GET_ALL_SALES
7. CREATE_PRODUCT
8. UPDATE_PRODUCT_PRICE
9. UPDATE_STOCK
10. CREATE_CATEGORY
11. CREATE_USER
12. BLOCK_USER
13. SALES_BY_EMPLOYEE
14. SALES_BY_CATEGORY
15. DASHBOARD_STATS

## Запуск
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=by.bsuir.electroshop.server.ElectroShopServer
mvn exec:java -Dexec.mainClass=by.bsuir.electroshop.client.ElectroShopClientApp
```

## Тестовые аккаунты
- admin / admin123
- seller / seller123
- manager / manager123
