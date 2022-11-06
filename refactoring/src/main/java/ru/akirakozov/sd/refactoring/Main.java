package ru.akirakozov.sd.refactoring;

import ru.akirakozov.sd.refactoring.db.ProductsDataBase;
import ru.akirakozov.sd.refactoring.utils.ServerUtils;

/**
 * @author akirakozov
 */
public class Main {
    public static void main(String[] args) throws Exception {
        ServerUtils.setupServer(new ProductsDataBase("jdbc:sqlite:test.db"));
    }
}
