package by.bsuir.electroshop.server.service;

import by.bsuir.electroshop.common.dto.ProductCreateRequest;
import by.bsuir.electroshop.common.dto.Response;
import by.bsuir.electroshop.common.dto.UpdatePriceRequest;
import by.bsuir.electroshop.common.dto.UpdateStockRequest;
import by.bsuir.electroshop.common.model.InventoryItem;
import by.bsuir.electroshop.server.repository.ProductRepository;

import java.sql.SQLException;
import java.util.ArrayList;

public class InventoryService {
    private final ProductRepository productRepository = new ProductRepository();

    public Response listProducts() {
        try {
            return Response.ok("Список товаров", new ArrayList<>(productRepository.findAll()));
        } catch (SQLException e) {
            return Response.error("Ошибка получения товаров: " + e.getMessage());
        }
    }

    public Response searchProducts(String keyword) {
        try {
            return Response.ok("Результаты поиска", new ArrayList<>(productRepository.search(keyword)));
        } catch (SQLException e) {
            return Response.error("Ошибка поиска: " + e.getMessage());
        }
    }

    public Response createProduct(ProductCreateRequest request) {
        try {
            InventoryItem item = new InventoryItem(0, request.categoryId(), null, request.brand(), request.model(),
                    request.technicalSpec(), request.retailPrice(), request.stockBalance(), request.warrantyMonths());
            long id = productRepository.create(item);
            return Response.ok("Товар создан. ID=" + id, null);
        } catch (SQLException e) {
            return Response.error("Ошибка создания товара: " + e.getMessage());
        }
    }
    public Response deleteProduct(int id) {
        try {
            productRepository.deleteById(id);
            return Response.ok("Товар удалён", null);
        } catch (RuntimeException e) {   // ← было: catch (Exception e)
            return Response.error("Ошибка удаления: " + e.getMessage());
        }
    }
    public Response updatePrice(UpdatePriceRequest request) {
        try {
            productRepository.updatePrice(request.productId(), request.newPrice());
            return Response.ok("Цена обновлена", null);
        } catch (SQLException e) {
            return Response.error("Ошибка обновления цены: " + e.getMessage());
        }
    }

    public Response updateStock(UpdateStockRequest request) {
        try {
            productRepository.changeStock(request.productId(), request.delta());
            return Response.ok("Остаток изменен", null);
        } catch (SQLException e) {
            return Response.error("Ошибка изменения остатка: " + e.getMessage());
        }
    }
}
