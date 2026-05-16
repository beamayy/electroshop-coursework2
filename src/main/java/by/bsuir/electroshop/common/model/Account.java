package by.bsuir.electroshop.common.model;

import by.bsuir.electroshop.common.enums.AccountStatus;
import by.bsuir.electroshop.common.enums.Role;

public class Account extends BaseEntity {
    private String username;
    private String passwordHash;
    private AccountStatus status;
    private Role role;

    public Account() {
    }

    public Account(long id, String username, String passwordHash, AccountStatus status, Role role) {
        super(id);
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
