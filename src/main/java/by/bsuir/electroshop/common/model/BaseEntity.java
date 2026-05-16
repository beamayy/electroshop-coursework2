package by.bsuir.electroshop.common.model;

import java.io.Serial;
import java.io.Serializable;

public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected long id;

    protected BaseEntity() {
    }

    protected BaseEntity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
