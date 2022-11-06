package ru.akirakozov.sd.refactoring.utils;

import ru.akirakozov.sd.refactoring.product.Product;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ResponseHtmlUtils {
    public static void writeResponse(HttpServletResponse response, String htmlResponse) throws IOException {
        response.getWriter()
            .println(htmlResponse);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public static String htmlResponse(Optional<String> headerOpt, String content) {
        return Stream.of(
            "<html><body>",
            headerOpt.map(header -> "<h1>" + header + "</h1>")
                .orElse(""),
            content,
            "</body></html>"
        ).filter(s -> !s.isEmpty())
            .collect(Collectors.joining("\n"));
    }

    public static String productsToHtml(List<Product> products) {
        return products
            .stream()
            .map(product -> product.getName() + "\t" + product.getPrice() + "</br>")
            .collect(Collectors.joining("\n"));
    }
}
