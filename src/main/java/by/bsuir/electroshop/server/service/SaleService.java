package by.bsuir.electroshop.server.service;

import by.bsuir.electroshop.common.dto.Response;
import by.bsuir.electroshop.common.dto.SaleRequest;
import by.bsuir.electroshop.common.model.Account;
import by.bsuir.electroshop.common.model.InventoryItem;
import by.bsuir.electroshop.common.model.TransactionItem;
import by.bsuir.electroshop.server.db.DatabaseManager;
import by.bsuir.electroshop.server.repository.AccountRepository;
import by.bsuir.electroshop.server.repository.ProductRepository;
import by.bsuir.electroshop.server.repository.SaleRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaleService {
    private final AccountRepository accountRepository = new AccountRepository();
    private final ProductRepository productRepository = new ProductRepository();
    private final SaleRepository saleRepository = new SaleRepository();

    public Response createSale(SaleRequest request) {
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            Optional<Account> optional = accountRepository.findByUsername(request.employeeUsername());
            if (optional.isEmpty()) {
                return Response.error("Сотрудник не найден");
            }
            if (request.items() == null || request.items().isEmpty()) {
                return Response.error("Список товаров пуст");
            }

            double total = 0;
            for (TransactionItem item : request.items()) {
                InventoryItem inventoryItem = productRepository.findById(item.getInventoryId(), connection)
                        .orElseThrow(() -> new IllegalArgumentException("Товар с ID=" + item.getInventoryId() + " не найден"));
                if (inventoryItem.getStockBalance() < item.getQuantity()) {
                    connection.rollback();
                    return Response.error("Недостаточно товара: " + inventoryItem.getBrand() + " " + inventoryItem.getModel());
                }
                total += item.getLineTotal();
            }

            long saleId = saleRepository.createSale(connection, optional.get().getId(), request.customerName(), total);
            for (TransactionItem item : request.items()) {
                saleRepository.createItem(connection, saleId, item);
                productRepository.changeStock(connection, item.getInventoryId(), -item.getQuantity());
            }
            connection.commit();
            return Response.ok("Продажа успешно оформлена. Сумма: " + total, null);
        } catch (IllegalArgumentException e) {
            return Response.error(e.getMessage());
        } catch (SQLException e) {
            return Response.error("Ошибка оформления продажи: " + e.getMessage());
        }
    }

    public Response getMySales(String username) {
        try {
            return Response.ok("История продаж сотрудника", new ArrayList<>(saleRepository.findSalesByUsername(username)));
        } catch (SQLException e) {
            return Response.error("Ошибка получения продаж: " + e.getMessage());
        }
    }

    public Response getAllSales() {
        try {
            return Response.ok("Все продажи", new ArrayList<>(saleRepository.findAllSales()));
        } catch (SQLException e) {
            return Response.error("Ошибка получения продаж: " + e.getMessage());
        }
    }
}
