import com.sun.net.httpserver.HttpHandler;	
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

//Dominic Cash
//16042439

/**
 * The {@code DeleteProductHandler} class handles HTTP requests for deleting products.
 * It supports:
 * GET requests: Displays a confirmation page for deleting a product.
 * POST requests: Deletes the product from the database and redirects the user to the admin panel.
 *
 * This class interacts with the {@link HomeApplianceDAO} to retrieve and delete product details
 * and uses the {@link Controller#parseQueryParams(String)} method for parsing HTTP query parameters.
 * Dependencies:
 * @link HomeApplianceDAO}
 * @link HomeAppliance}
 * {@link Controller}
 *
 * @author Dominic Cash
 */
public class DeleteProductHandler implements HttpHandler {

    private HomeApplianceDAO applianceDAO = new HomeApplianceDAO();

    /**
     * Handles incoming HTTP requests to the /deleteProduct endpoint.
     * Depending on the request method, either displays a confirmation page (GET)
     * or deletes the product and redirects to the admin panel (POST).
     *
     * @param exchange the {@link HttpExchange} object containing the request and response
     * @throws IOException if an I/O error occurs during processing
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean isLoggedIn = checkLoginStatus(exchange);
        if (!isLoggedIn) {
            exchange.getResponseHeaders().set("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            URI uri = exchange.getRequestURI();
            String query = uri.getQuery();
            int applianceId = Integer.parseInt(query.split("=")[1]);

            HomeAppliance appliance = applianceDAO.findProduct(applianceId);

            showConfirmationPage(exchange, appliance);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            String formData = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> params = Controller.parseQueryParams(formData);
            int applianceId = Integer.parseInt(params.get("id"));

            applianceDAO.deleteItem(applianceId);

            exchange.getResponseHeaders().set("Location", "/adminPanel");
            exchange.sendResponseHeaders(302, -1);
        }
    }

    /**
     * Displays the confirmation page for deleting a product.
     *
     * @param exchange  the {@link HttpExchange} object containing the request and response
     * @param appliance the {@link HomeAppliance} object containing the product details
     * @throws IOException if an I/O error occurs during processing
     */
    private void showConfirmationPage(HttpExchange exchange, HomeAppliance appliance) throws IOException {
        String response = String.format("""
        <html>
        <head><title>Delete Product</title></head>
        <body>
            <h1>You have selected '%s' to delete. Are you sure?</h1>
            <form method="POST" action="/deleteProduct">
                <input type="hidden" name="id" value="%d" />
                <button type="submit">Yes, Delete</button>
            </form>
            <form method="GET" action="/adminPanel">
                <button type="submit">No, Cancel</button>
            </form>
        </body>
        </html>
    """, appliance.getDescription(), appliance.getId());

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Mocked method to check login status.
     *
     * @param exchange the {@link HttpExchange} object containing the request and response
     * @return {@code true} if the user is logged in; {@code false} otherwise
     */
    private boolean checkLoginStatus(HttpExchange exchange) {
        return true;
    }
}