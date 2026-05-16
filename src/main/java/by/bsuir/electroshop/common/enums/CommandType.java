package by.bsuir.electroshop.common.enums;

import java.io.Serializable;

public enum CommandType implements Serializable {
    LOGIN,
    LIST_PRODUCTS,
    SEARCH_PRODUCTS,
    CREATE_SALE,
    GET_MY_SALES,
    GET_ALL_SALES,
    CREATE_PRODUCT,
    UPDATE_PRODUCT_PRICE,
    UPDATE_STOCK,
    CREATE_CATEGORY,
    CREATE_USER,
    BLOCK_USER,
    SALES_BY_EMPLOYEE,
    SALES_BY_CATEGORY,
    DASHBOARD_STATS
}
