import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
//Dominic Cash
//16042439

/**
 * Handles the process of displaying and processing the form for adding a new product.
 * This class implements the {@link HttpHandler} interface to handle HTTP requests.
 * It handles both GET requests (to display the form) and POST requests (to process the form data).
 *
 * This handler is used to add a new product to the database via an HTML form.
 *
 * @see HttpHandler
 */
public class AddProductHandler implements HttpHandler {

    /**
     * Handles the HTTP request by either displaying the form or processing the form submission.
     *
     * If the request method is GET, it displays the form to add a new product. If the request method
     * is POST, it processes the submitted form data to add the new product to the database. If the method
     * is neither GET nor POST, it returns a 405 Method Not Allowed response.
     *
     * @param exchange The HttpExchange object containing the request and response information.
     * @throws IOException If an I/O error occurs during the request handling process.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html");

        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            displayAddProductForm(exchange); 
        } else if ("POST".equalsIgnoreCase(method)) {
            processAddProductForm(exchange); 
        } else {
            exchange.sendResponseHeaders(405, -1); 
        }
    }

    /**
     * Displays the form for adding a new product.
     *
     * This method generates an HTML form where the admin can input product details such as SKU, description,
     * category, and price. The form submits data to the server using the POST method to add the product.
     *
     * @param exchange The HttpExchange object containing the request and response information.
     * @throws IOException If an I/O error occurs while writing the response.
     */
    private void displayAddProductForm(HttpExchange exchange) throws IOException {
        String response = """
            <html>
            <head><title>Add New Product</title></head>
            <body>
                <h1>Add New Product</h1>
                <form action="/addProduct" method="POST">
                    <label for="sku">SKU:</label>
                    <input type="text" name="sku" required><br>
                    <label for="description">Description:</label>
                    <input type="text" name="description" required><br>
                    <label for="category">Category:</label>
                    <input type="text" name="category" required><br>
                    <label for="price">Price:</label>
                    <input type="number" name="price" required><br>
                    <button type="submit">Add Product</button>
                </form>
                <p><a href="/adminpanel">Back to Admin Panel</a></p>
            </body>
            </html>
        """;

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Processes the form submission for adding a new product to the database.
     *
     * This method reads the form data from the POST request, parses the parameters, and inserts the product
     * details into the database. After the product is added, a confirmation message is displayed to the user.
     *
     * @param exchange The HttpExchange object containing the request and response information.
     * @throws IOException If an I/O error occurs while reading the request body or writing the response.
     */
    private void processAddProductForm(HttpExchange exchange) throws IOException {
        // Read the form data from the request body
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String formData = reader.readLine();

        Map<String, String> params = Controller.parseQueryParams(formData);

        String sku = params.get("sku");
        String description = params.get("description");
        String category = params.get("category");
        int price = Integer.parseInt(params.get("price"));

        addProductToDatabase(sku, description, category, price);

        String response = "<html><body><h1>Product added successfully!</h1><a href='/adminPanel'>Back to Admin Panel</a></body></html>";

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Inserts a new product into the database.
     *
     * This method connects to the database and executes an SQL query to insert the product details
     * (SKU, description, category, and price) into the appliance table.
     *
     * @param sku The SKU of the product.
     * @param description The description of the product.
     * @param category The category of the product.
     * @param price The price of the product.
     */
    private void addProductToDatabase(String sku, String description, String category, int price) {
        String dbUrl = "jdbc:sqlite:stores.sqlite";
        String query = "INSERT INTO appliance (sku, description, category, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sku);
            stmt.setString(2, description);
            stmt.setString(3, category);
            stmt.setInt(4, price);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
