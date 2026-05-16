package by.bsuir.electroshop.server.handler;

import by.bsuir.electroshop.common.dto.*;
import by.bsuir.electroshop.common.enums.CommandType;
import by.bsuir.electroshop.common.enums.Role;
import by.bsuir.electroshop.common.model.Account;
import by.bsuir.electroshop.server.repository.AccountRepository;
import by.bsuir.electroshop.server.service.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class RequestHandlerFactory {
    private final Map<CommandType, RequestHandler> handlers = new EnumMap<>(CommandType.class);
    private final AuthService authService = new AuthService();
    private final InventoryService inventoryService = new InventoryService();
    private final SaleService saleService = new SaleService();
    private final AdminService adminService = new AdminService();
    private final ReportService reportService = new ReportService();
    private final AccountRepository accountRepository = new AccountRepository();

    public RequestHandlerFactory() {
        handlers.put(CommandType.LOGIN, request -> authService.login((LoginRequest) request.getPayload()));
        handlers.put(CommandType.LIST_PRODUCTS, request -> inventoryService.listProducts());
        handlers.put(CommandType.SEARCH_PRODUCTS, request -> inventoryService.searchProducts((String) request.getPayload()));
        handlers.put(CommandType.CREATE_SALE, request -> requireRole(request, new Role[]{Role.ADMIN, Role.MANAGER, Role.SELLER},
                () -> saleService.createSale((SaleRequest) request.getPayload())));
        handlers.put(CommandType.GET_MY_SALES, request -> requireRole(request, new Role[]{Role.ADMIN, Role.MANAGER, Role.SELLER},
                () -> saleService.getMySales(request.getSessionUser())));
        handlers.put(CommandType.GET_ALL_SALES, request -> requireRole(request, new Role[]{Role.ADMIN, Role.MANAGER}, saleService::getAllSales));
        handlers.put(CommandType.CREATE_PRODUCT, request -> requireRole(request, new Role[]{Role.ADMIN, Role.MANAGER},
                () -> inventoryService.createProduct((ProductCreateRequest) request.getPayload())));
        handlers.put(CommandType.UPDATE_PRODUCT_PRICE, request -> requireRole(request, new Role[]{Role.ADMIN, Role.MANAGER},
                () -> inventoryService.updatePrice((UpdatePriceRequest) request.getPayload())));
        handlers.put(CommandType.UPDATE_STOCK, request -> requireRole(request, new Role[]{Role.ADMIN, Role.MANAGER},
                () -> inventoryService.updateStock((UpdateStockRequest) request.getPayload())));
        handlers.put(CommandType.CREATE_CATEGORY, request -> requireRole(request, new Role[]{Role.ADMIN},
                () -> adminService.createCategory((CategoryCreateRequest) request.getPayload())));
        handlers.put(CommandType.CREATE_USER, request -> requireRole(request, new Role[]{Role.ADMIN},
                () -> adminService.createUser((UserCreateRequest) request.getPayload())));
        handlers.put(CommandType.BLOCK_USER, request -> requireRole(request, new Role[]{Role.ADMIN},
                () -> adminService.blockUser((String) request.getPayload())));
        handlers.put(CommandType.SALES_BY_EMPLOYEE, request -> requireRole(request, new Role[]{Role.ADMIN, Role.MANAGER}, reportService::salesByEmployee));
        handlers.put(CommandType.SALES_BY_CATEGORY, request -> requireRole(request, new Role[]{Role.ADMIN, Role.MANAGER}, reportService::salesByCategory));
        handlers.put(CommandType.DASHBOARD_STATS, request -> requireRole(request, new Role[]{Role.ADMIN, Role.MANAGER}, reportService::dashboardStats));
    }

    public Response dispatch(Request request) {
        RequestHandler handler = handlers.get(request.getCommandType());
        if (handler == null) {
            return Response.error("Неизвестная команда: " + request.getCommandType());
        }
        return handler.handle(request);
    }

    private Response requireRole(Request request, Role[] roles, ProtectedSupplier supplier) {
        if (request.getSessionUser() == null || request.getSessionUser().isBlank()) {
            return Response.error("Требуется авторизация");
        }
        try {
            Optional<Account> optional = accountRepository.findByUsername(request.getSessionUser());
            if (optional.isEmpty()) {
                return Response.error("Пользователь сессии не найден");
            }
            Account account = optional.get();
            boolean allowed = Arrays.asList(roles).contains(account.getRole());
            if (!allowed) {
                return Response.error("Недостаточно прав для выполнения команды");
            }
            return supplier.get();
        } catch (SQLException e) {
            return Response.error("Ошибка проверки роли: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface ProtectedSupplier {
        Response get();
    }
}
