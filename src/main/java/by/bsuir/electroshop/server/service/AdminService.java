package by.bsuir.electroshop.server.service;

import by.bsuir.electroshop.common.dto.*;
import by.bsuir.electroshop.server.repository.AccountRepository;
import by.bsuir.electroshop.server.repository.CategoryRepository;

import java.sql.SQLException;
import java.util.ArrayList;

public class AdminService {
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final AccountRepository accountRepository = new AccountRepository();

    public Response createCategory(CategoryCreateRequest request) {
        try {
            long id = categoryRepository.create(request.name(), request.description());
            return Response.ok("Категория создана. ID=" + id, null);
        } catch (SQLException e) {
            return Response.error("Ошибка создания категории: " + e.getMessage());
        }
    }

    public Response listCategories() {
        try {
            return Response.ok("Список категорий", new ArrayList<>(categoryRepository.findAll()));
        } catch (SQLException e) {
            return Response.error("Ошибка чтения категорий: " + e.getMessage());
        }
    }

    public Response createUser(UserCreateRequest request) {
        try {
            long accountId = accountRepository.createUser(
                    request.username(),
                    PasswordHasher.sha1(request.password()),
                    request.role()
            );
            accountRepository.createProfile(accountId, request.firstName(), request.lastName(), request.phone());
            return Response.ok("Пользователь создан. ID=" + accountId, null);
        } catch (SQLException e) {
            return Response.error("Ошибка создания пользователя: " + e.getMessage());
        }
    }

    public Response blockUser(String username) {
        try {
            accountRepository.blockUser(username);
            return Response.ok("Пользователь заблокирован", null);
        } catch (SQLException e) {
            return Response.error("Ошибка блокировки пользователя: " + e.getMessage());
        }
    }
}
