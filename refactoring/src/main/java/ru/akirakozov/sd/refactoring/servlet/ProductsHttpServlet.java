package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.db.ProductsDataBase;

import javax.servlet.http.HttpServlet;

public abstract class ProductsHttpServlet extends HttpServlet {
    protected final ProductsDataBase dataBase;
    public ProductsHttpServlet(final ProductsDataBase dataBase) {
        this.dataBase = dataBase;
    }
}
