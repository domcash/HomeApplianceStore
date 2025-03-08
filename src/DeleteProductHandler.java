/**
 * The {@code DeleteProductHandler} class handles HTTP requests for deleting products.
 * It supports:
 * GET requests: Displays a confirmation page for deleting a product.
 * POST requests: Deletes the product from the database and redirects the user to the admin panel.
 *
 * This class interacts with the {@link HomeApplianceDAO} to retrieve and delete product details
 * and uses the {@link Controller#parseQueryParams(String)} method for parsing HTTP query parameters.
 *
 * Dependencies:
 * {@link HomeApplianceDAO}
 * {@link HomeAppliance}
 * {@link Controller}
 *
 * @author Dominic Cash
 */
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
            <head>
                <title>Home Solutions - Delete Product</title>
                <meta charset='UTF-8'>
                <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>
                <style>
                    body {
                        font-family: 'Poppins', sans-serif;
                        background: linear-gradient(135deg, #f0f4f8, #d9e2ec);
                        margin: 0;
                        padding: 0;
                        color: #333;
                    }
                    .container {
                        max-width: 600px;
                        margin: 100px auto;
                        padding: 20px;
                        background: #fff;
                        border-radius: 10px;
                        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                        animation: fadeIn 1s ease-in;
                        text-align: center;
                    }
                    @keyframes fadeIn {
                        from { opacity: 0; }
                        to { opacity: 1; }
                    }
                    h1 {
                        color: #2c3e50;
                        font-size: 2em;
                        text-transform: uppercase;
                        letter-spacing: 2px;
                        margin-bottom: 20px;
                    }
                    form {
                        display: inline-block;
                        margin: 10px;
                    }
                    .btn {
                        background: #2ecc71;
                        color: white;
                        padding: 12px 25px;
                        text-align: center;
                        border: none;
                        cursor: pointer;
                        text-decoration: none;
                        display: inline-block;
                        border-radius: 25px;
                        font-weight: 600;
                        transition: transform 0.2s, background 0.3s;
                    }
                    .btn:hover {
                        background: #27ae60;
                        transform: translateY(-2px);
                    }
                    .btn.cancel {
                        background: #e74c3c;
                    }
                    .btn.cancel:hover {
                        background: #c0392b;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>You have selected '%s' to delete. Are you sure?</h1>
                    <form method="POST" action="/deleteProduct">
                        <input type="hidden" name="id" value="%d" />
                        <button type="submit" class="btn">Yes, Delete</button>
                    </form>
                    <form method="GET" action="/adminPanel">
                        <button type="submit" class="btn cancel">No, Cancel</button>
                    </form>
                </div>
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
     * Checks whether the user is logged in by inspecting the session cookie.
     *
     * @param exchange the {@link HttpExchange} object containing the request headers
     * @return {@code true} if the user is logged in; {@code false} otherwise
     */
    private boolean checkLoginStatus(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null && cookieHeader.contains("session=")) {
            return true;
        }
        return false; // Updated to properly check login status
    }
}