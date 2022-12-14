package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.db.ProductsDataBase;
import ru.akirakozov.sd.refactoring.utils.ResponseHtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class AddProductServlet extends ProductsHttpServlet {

    public AddProductServlet(ProductsDataBase dataBase) {
        super(dataBase);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        int price = Integer.parseInt(request.getParameter("price"));
        dataBase.addProduct(name, price);
        ResponseHtmlUtils.writeResponse(response, "OK");
    }
}
