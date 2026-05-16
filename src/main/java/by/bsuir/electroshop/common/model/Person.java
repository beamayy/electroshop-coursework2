package by.bsuir.electroshop.common.model;

public abstract class Person extends BaseEntity {
    protected String firstName;
    protected String lastName;

    protected Person() {
    }

    protected Person(long id, String firstName, String lastName) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
