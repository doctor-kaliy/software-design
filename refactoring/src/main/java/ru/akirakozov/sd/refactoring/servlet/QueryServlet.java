package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.db.ProductsDataBase;
import ru.akirakozov.sd.refactoring.product.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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

        if ("max".equals(command)) {
            List<Product> maxProducts = dataBase.getMax();

            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with max price: </h1>");

            for (Product product : maxProducts) {
                response.getWriter().println(product.getName() + "\t" + product.getPrice() + "</br>");
            }

            response.getWriter().println("</body></html>");
        } else if ("min".equals(command)) {
            List<Product> minProducts = dataBase.getMin();

            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with min price: </h1>");

            for (Product product : minProducts) {
                response.getWriter().println(product.getName() + "\t" + product.getPrice() + "</br>");
            }

            response.getWriter().println("</body></html>");
        } else if ("sum".equals(command)) {
           List<Integer> sum = dataBase.getSum();

            response.getWriter().println("<html><body>");
            response.getWriter().println("Summary price: ");

            if (!sum.isEmpty()) {
                response.getWriter().println(sum.get(0));
            }

            response.getWriter().println("</body></html>");
        } else if ("count".equals(command)) {
            List<Integer> count = dataBase.getCount();

            response.getWriter().println("<html><body>");
            response.getWriter().println("Number of products: ");

            if (!count.isEmpty()) {
                response.getWriter().println(count.get(0));
            }

            response.getWriter().println("</body></html>");
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
