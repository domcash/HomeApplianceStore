/**
 * Handles the process of displaying and processing the form for adding a new product.
 * This class implements the {@link HttpHandler} interface to handle HTTP requests.
 * It handles both GET requests (to display the form) and POST requests (to process the form data).
 *
 * This handler is used to add a new product to the database via an HTML form.
 *
 * @see HttpHandler
 * @author Dominic Cash
 */
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
            <head>
                <title>Home Solutions - Add New Product</title>
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
                        margin: 60px auto;
                        padding: 20px;
                        background: #fff;
                        border-radius: 10px;
                        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                        animation: fadeIn 1s ease-in;
                    }
                    @keyframes fadeIn {
                        from { opacity: 0; }
                        to { opacity: 1; }
                    }
                    h1 {
                        text-align: center;
                        color: #2c3e50;
                        font-size: 2em;
                        text-transform: uppercase;
                        letter-spacing: 2px;
                        margin-bottom: 20px;
                    }
                    form {
                        display: flex;
                        flex-direction: column;
                        gap: 15px;
                    }
                    label {
                        font-weight: 600;
                        margin-bottom: 5px;
                    }
                    input[type='text'], input[type='number'] {
                        width: 100%;
                        padding: 12px;
                        border-radius: 8px;
                        border: 1px solid #ddd;
                        font-size: 1em;
                        background: #fafafa;
                        box-sizing: border-box;
                        transition: border-color 0.3s;
                    }
                    input[type='text']:focus, input[type='number']:focus {
                        border-color: #3498db;
                        outline: none;
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
                    .footer {
                        display: flex;
                        justify-content: center;
                        gap: 20px;
                        margin-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Add New Product</h1>
                    <form action="/addProduct" method="POST">
                        <label for="sku">SKU:</label>
                        <input type="text" name="sku" placeholder="Enter SKU" required>
                        <label for="description">Description:</label>
                        <input type="text" name="description" placeholder="Enter description" required>
                        <label for="category">Category:</label>
                        <input type="text" name="category" placeholder="Enter category" required>
                        <label for="price">Price:</label>
                        <input type="number" name="price" placeholder="Enter price" required>
                        <button type="submit" class="btn">Add Product</button>
                    </form>
                    <div class="footer">
                        <a href="/adminPanel" class="btn">Back to Admin Panel</a>
                    </div>
                </div>
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
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String formData = reader.readLine();

        Map<String, String> params = Controller.parseQueryParams(formData);

        String sku = params.get("sku");
        String description = params.get("description");
        String category = params.get("category");
        int price = Integer.parseInt(params.get("price"));

        addProductToDatabase(sku, description, category, price);

        String response = """
            <html>
            <head>
                <title>Home Solutions - Product Added</title>
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
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Product Added Successfully!</h1>
                    <p><a href="/adminPanel" class="btn">Back to Admin Panel</a></p>
                </div>
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
