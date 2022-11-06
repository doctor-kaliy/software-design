package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.db.ProductsDataBase;
import ru.akirakozov.sd.refactoring.utils.ResponseHtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author akirakozov
 */
public class QueryServlet extends ProductsHttpServlet {

    public QueryServlet(ProductsDataBase dataBase) {
        super(dataBase);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        String responseBody = "";
        if ("max".equals(command)) {
            responseBody = ResponseHtmlUtils.htmlResponse(
                    Optional.of("Product with max price: "),
                    ResponseHtmlUtils.productsToHtml(dataBase.getMax()));
        } else if ("min".equals(command)) {
            responseBody = ResponseHtmlUtils.htmlResponse(
                    Optional.of("Product with min price: "),
                    ResponseHtmlUtils.productsToHtml(dataBase.getMin()));
        } else if ("sum".equals(command)) {
            responseBody = ResponseHtmlUtils.htmlResponse(
                    Optional.empty(),
                    "Summary price: \n" +
                            dataBase.getSum()
                                    .stream()
                                    .map(sum -> Integer.toString(sum))
                                    .collect(Collectors.joining("")));
        } else if ("count".equals(command)) {
            responseBody = ResponseHtmlUtils.htmlResponse(
                    Optional.empty(),
                    "Number of products: \n" +
                            dataBase.getCount()
                                    .stream()
                                    .map(count -> Integer.toString(count))
                                    .collect(Collectors.joining("")));
        }
        ResponseHtmlUtils.writeResponse(response, responseBody);
    }

}
