package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.db.ProductsDataBase;
import ru.akirakozov.sd.refactoring.utils.ResponseHtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends ProductsHttpServlet {

    public GetProductsServlet(ProductsDataBase dataBase) {
        super(dataBase);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseHtmlUtils.writeResponse(
                response,
                ResponseHtmlUtils.htmlResponse(
                        Optional.empty(),
                        ResponseHtmlUtils.productsToHtml(dataBase.getProducts()))
        );
    }
}
