package by.bsuir.electroshop.common.model;

public class StaffProfile extends Person {
    private long accountId;
    private String phone;

    public StaffProfile() {
    }

    public StaffProfile(long id, long accountId, String firstName, String lastName, String phone) {
        super(id, firstName, lastName);
        this.accountId = accountId;
        this.phone = phone;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getPhone() {
        return phone;
    }
}
