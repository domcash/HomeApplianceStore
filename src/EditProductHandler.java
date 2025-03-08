/**
 * The {@code EditProductHandler} class is responsible for handling HTTP requests related to
 * editing product details in an ecommerce application. The handler supports both GET and POST
 * requests for viewing and updating product information, respectively.
 *
 * GET requests generate an HTML form prefilled with the current product details, while POST
 * requests process the form submission and update the product in the database.
 *
 * Dependencies:
 * {@link HomeApplianceDAO} for database interactions.
 * {@link HomeAppliance} for representing product entities.
 * {@link Controller#parseQueryParams(String)} for parsing query parameters.
 *
 * This handler is registered at the "/editProduct" endpoint.
 *
 * @author Dominic Cash
 */
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EditProductHandler implements HttpHandler {

    /**
     * Handles incoming HTTP requests for the /editProduct endpoint.
     * Supports GET for rendering the product editing form and POST for updating the product in the database.
     *
     * @param exchange the {@link HttpExchange} object representing the HTTP request and response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = Controller.parseQueryParams(query);
            String id = params.get("id");

            HomeApplianceDAO applianceDAO = new HomeApplianceDAO();
            HomeAppliance appliance = applianceDAO.findProduct(Integer.parseInt(id));

            StringBuilder response = new StringBuilder();
            response.append("""
                <html>
                <head>
                    <title>Home Solutions - Edit Product</title>
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
                        <h1>Edit Product</h1>
                        <form method='POST' action='/editProduct'>
                            <input type='hidden' name='id' value='%d' />
                            <label for="sku">SKU:</label>
                            <input type='text' name='sku' value='%s' required />
                            <label for="description">Description:</label>
                            <input type='text' name='description' value='%s' required />
                            <label for="category">Category:</label>
                            <input type='text' name='category' value='%s' required />
                            <label for="price">Price:</label>
                            <input type='number' name='price' value='%d' required />
                            <button type='submit' class='btn'>Update Product</button>
                        </form>
                        <div class="footer">
                            <a href="/adminPanel" class="btn">Back to Admin Panel</a>
                        </div>
                    </div>
                </body>
                </html>
            """.formatted(appliance.getId(), appliance.getSku(), appliance.getDescription(), appliance.getCategory(), appliance.getPrice()));

            byte[] responseBytes = response.toString().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String formData = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> params = Controller.parseQueryParams(formData);

            int id = Integer.parseInt(params.get("id"));
            String sku = params.get("sku");
            String description = params.get("description");
            String category = params.get("category");
            int price = Integer.parseInt(params.get("price"));

            HomeAppliance updatedAppliance = new HomeAppliance(id, sku, description, category, price);
            HomeApplianceDAO applianceDAO = new HomeApplianceDAO();

            boolean success = applianceDAO.updateItem(updatedAppliance);

            StringBuilder response = new StringBuilder();
            response.append("""
                <html>
                <head>
                    <title>Home Solutions - Product Update</title>
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
            """);

            if (success) {
                response.append("<h1>Product Updated Successfully!</h1>");
            } else {
                response.append("<h1>Failed to Update Product</h1>");
            }
            response.append("<p><a href='/adminPanel' class='btn'>Back to Admin Panel</a></p>")
                    .append("</div>")
                    .append("</body>")
                    .append("</html>");

            byte[] responseBytes = response.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
}