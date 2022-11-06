package ru.akirakozov.sd.refactoring.utils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.akirakozov.sd.refactoring.db.ProductsDataBase;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

public final class ServerUtils {
    public static void setupServer(final ProductsDataBase dataBase) throws Exception {
        dataBase.createTable();

        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet(dataBase)), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet(dataBase)),"/get-products");
        context.addServlet(new ServletHolder(new QueryServlet(dataBase)),"/query");

        server.start();
        server.join();
    }
}
