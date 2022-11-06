package ru.akirakozov.sd.refactoring.db;

import ru.akirakozov.sd.refactoring.product.Product;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductsDataBase {
    private final String connectionURI;

    public ProductsDataBase(final String connectionURI) {
        this.connectionURI = connectionURI;
    }

    private void executeUpdate(String sql) {
        try {
            try (Connection connection = DriverManager.getConnection(connectionURI)) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(sql);
                }
            }
        } catch (SQLException cause) {
            throw new RuntimeException(cause);
        }
    }

    private interface ResultGetter<T> {
        T getResult(ResultSet resultSet) throws SQLException;
    }

    private final ResultGetter<Product> GET_PRODUCT = resultSet -> {
        String name = resultSet.getString("name");
        int price = resultSet.getInt("price");
        return new Product(name, price);
    };

    private final ResultGetter<Integer> GET_INT = resultSet -> resultSet.getInt(1);

    private <T> List<T> executeQuery(String sql, ResultGetter<T> resultGetter) {
        List<T> result = new ArrayList<>();

        try {
            try (Connection connection = DriverManager.getConnection(connectionURI)) {
                try (Statement stmt = connection.createStatement()) {
                    try(ResultSet resultSet = stmt.executeQuery(sql)) {
                        while (resultSet.next()) {
                            result.add(resultGetter.getResult(resultSet));
                        }
                    }
                }
            }
        } catch (SQLException cause) {
            throw new RuntimeException(cause);
        }

        return result;
    }

    public void createTable() {
        executeUpdate("CREATE TABLE IF NOT EXISTS PRODUCT" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " PRICE          INT     NOT NULL)");
    }

    public void addProduct(final String name, final int price) {
        executeUpdate("INSERT INTO PRODUCT " +
                "(NAME, PRICE) VALUES (\"" + name + "\"," + price + ")");
    }

    public void clear() {
        executeUpdate("DELETE FROM PRODUCT");
    }

    public List<Product> getProducts() {
        return executeQuery("SELECT * FROM PRODUCT", GET_PRODUCT);
    }

    public List<Product> getMax() {
        return executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1", GET_PRODUCT);
    }

    public List<Product> getMin() {
        return executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1", GET_PRODUCT);
    }

    public List<Integer> getSum() {
        return executeQuery("SELECT SUM(price) FROM PRODUCT", GET_INT);
    }

    public List<Integer> getCount() {
        return executeQuery("SELECT COUNT(*) FROM PRODUCT", GET_INT);
    }

}
