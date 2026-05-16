package by.bsuir.electroshop.server.service;

import by.bsuir.electroshop.common.dto.LoginRequest;
import by.bsuir.electroshop.common.dto.LoginResponse;
import by.bsuir.electroshop.common.dto.Response;
import by.bsuir.electroshop.common.enums.AccountStatus;
import by.bsuir.electroshop.common.model.Account;
import by.bsuir.electroshop.server.repository.AccountRepository;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final AccountRepository accountRepository = new AccountRepository();

    public Response login(LoginRequest request) {
        try {
            Optional<Account> optional = accountRepository.findByUsername(request.username());
            if (optional.isEmpty()) {
                return Response.error("Пользователь не найден");
            }
            Account account = optional.get();
            if (account.getStatus() == AccountStatus.BLOCKED) {
                return Response.error("Учетная запись заблокирована");
            }
            if (!account.getPasswordHash().equals(PasswordHasher.sha1(request.password()))) {
                return Response.error("Неверный пароль");
            }
            return Response.ok("Авторизация выполнена",
                    new LoginResponse(account.getId(), account.getUsername(), account.getRole()));
        } catch (SQLException e) {
            return Response.error("Ошибка авторизации: " + e.getMessage());
        }
    }
}
