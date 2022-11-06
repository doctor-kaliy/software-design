import org.junit.*;
import ru.akirakozov.sd.refactoring.db.ProductsDataBase;
import ru.akirakozov.sd.refactoring.utils.ServerUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTest {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String TEST_DB = "jdbc:sqlite:test.db";
    private static Connection connection = null;

    private static final class RequestBuilder {

        private static final String MAIN_URI = "http://localhost:8081";
        private static final String GET_PRODUCTS = "get-products";
        private static final String ADD_PRODUCT = "add-product";
        private static final String QUERY = "query";

        private final StringBuilder uri;
        private boolean isFirstParameter = true;

        private RequestBuilder(String mainURI) {
            uri = new StringBuilder(mainURI);
        }

        private RequestBuilder addMethod(String method) {
            uri.append("/")
                .append(method);
            return this;
        }

        private RequestBuilder addParameter(String parameter, String value) {
            if (isFirstParameter) {
                uri.append("?");
            } else {
                uri.append("&");
            }

            uri.append(parameter)
                .append("=")
                .append(value);

            isFirstParameter = false;
            return this;
        }

        private String uri() {
            return uri.toString();
        }
    }
    private static final HttpClient client =
        HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    private static String sendRequest(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(uri))
                .build();
        return client
            .send(request, HttpResponse.BodyHandlers.ofString())
            .body();
    }

    private static String addProduct(String name, String value) throws IOException, InterruptedException {
        String uri = new RequestBuilder(RequestBuilder.MAIN_URI)
                .addMethod(RequestBuilder.ADD_PRODUCT)
                .addParameter("name", name)
                .addParameter("price", value)
                .uri();
        return sendRequest(uri);
    }

    private static String getProducts() throws IOException, InterruptedException {
        String uri = new RequestBuilder(RequestBuilder.MAIN_URI)
                .addMethod(RequestBuilder.GET_PRODUCTS)
                .uri();
        return sendRequest(uri);
    }

    private static String query(String command) throws IOException, InterruptedException {
        String uri = new RequestBuilder(RequestBuilder.MAIN_URI)
                .addMethod(RequestBuilder.QUERY)
                .addParameter("command", command)
                .uri();
        return sendRequest(uri);
    }

    @BeforeClass
    public static void beforeAll() throws InterruptedException, SQLException {
        connection = DriverManager.getConnection(TEST_DB);
        executorService.submit(() -> {
            try {
                ServerUtils.setupServer(new ProductsDataBase(TEST_DB));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        Thread.sleep(1000);
    }

    @Before
    public void beforeTest() throws SQLException {
        String sql = "DELETE FROM PRODUCT";
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    @AfterClass
    public static void afterALl() throws SQLException {
        executorService.shutdown();
        connection.close();
    }

    @Test
    public void noErrorsTest() {
        Assert.assertTrue(true);
    }

    @Test
    public void addProductTest() throws IOException, InterruptedException {
        Assert.assertEquals("OK\n", addProduct("Healing_Salve", "100"));
    }

    @Test
    public void getProductsTest() throws IOException, InterruptedException {
        addProduct("Healing_Salve", "100");
        addProduct("Mask_of_Madness", "1100");
        addProduct("Enchanted_Mango", "75");

        String expected =
            "<html><body>\n" +
            "Healing_Salve\t100</br>\n" +
            "Mask_of_Madness\t1100</br>\n" +
            "Enchanted_Mango\t75</br>\n" +
            "</body></html>\n";
        String actual = getProducts();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void querySumTest() throws IOException, InterruptedException {
        addProduct("Healing_Salve", "100");
        addProduct("Mask_of_Madness", "1100");

        String expected =
            "<html><body>\n" +
            "Summary price: \n" +
            "1200\n" +
            "</body></html>\n";
        String actual = query("sum");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void queryCountTest() throws IOException, InterruptedException {
        addProduct("Healing_Salve", "100");
        addProduct("Mask_of_Madness", "1100");

        String expected =
            "<html><body>\n" +
            "Number of products: \n" +
            "2\n" +
            "</body></html>\n";
        String actual = query("count");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void queryMaxTest() throws IOException, InterruptedException {
        addProduct("Healing_Salve", "100");
        addProduct("Mask_of_Madness", "1100");

        String expected =
            "<html><body>\n" +
            "<h1>Product with max price: </h1>\n" +
            "Mask_of_Madness\t1100</br>\n" +
            "</body></html>\n";
        String actual = query("max");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void queryMinTest() throws IOException, InterruptedException {
        addProduct("Divine_Rapier", "5950");
        addProduct("Healing_Salve", "100");
        addProduct("Mask_of_Madness", "1100");

        String expected =
            "<html><body>\n" +
            "<h1>Product with min price: </h1>\n" +
            "Healing_Salve\t100</br>\n" +
            "</body></html>\n";
        String actual = query("min");
        Assert.assertEquals(expected, actual);
    }
}
