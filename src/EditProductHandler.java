import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The EditProductHandler class handles HTTP requests related to editing a home appliance product.
 * It processes both GET requests to show the edit form and POST requests to update the product in the database.
 * <p>
 * GET requests: Loads the existing product details based on the product ID and displays them in a form.
 * POST requests: Accepts updated product data from the form and saves it to the database.
 * </p>
 */
public class EditProductHandler implements HttpHandler {

    /**
     * Handles incoming HTTP requests and delegates to the appropriate method based on the HTTP method.
     *
     * @param exchange The HttpExchange object that encapsulates the HTTP request and response.
     * @throws IOException If an input or output error occurs during processing.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleGet(exchange);
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            handlePost(exchange);
        }
    }

    /**
     * Handles GET requests for editing a product.
     * Retrieves the product using the 'id' query parameter and displays a form with its details pre-filled.
     *
     * @param exchange The HttpExchange object containing the GET request.
     * @throws IOException If an input or output error occurs during processing.
     */
    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = Controller.parseQueryParams(query);
        String id = params.get("id");

        try {
            HomeApplianceDAO applianceDAO = new HomeApplianceDAO();
            HomeAppliance appliance = applianceDAO.findProduct(Integer.parseInt(id));

            String response = String.format("""
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
                            width: 100%%;
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
            """, appliance.getId(), appliance.getSku(), appliance.getDescription(), appliance.getCategory(), appliance.getPrice());

            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, """
                <html>
                <head>
                    <title>Home Solutions - Error</title>
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
                        <h1>Error Loading Product for Editing</h1>
                        <p><a href="/adminPanel" class="btn">Back to Admin Panel</a></p>
                    </div>
                </body>
                </html>
            """);
            e.printStackTrace();
        }
    }

    /**
     * Handles POST requests to update a product.
     * Parses the submitted form data and updates the product in the database.
     *
     * @param exchange The HttpExchange object containing the POST request.
     * @throws IOException If an input or output error occurs during processing.
     */
    private void handlePost(HttpExchange exchange) throws IOException {
        String formData = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = parseFormData(formData);

        System.out.println("POST params: " + params); // Debug log

        try {
            int id = Integer.parseInt(params.get("id"));
            String sku = params.get("sku");
            String description = params.get("description");
            String category = params.get("category");
            int price = Integer.parseInt(params.get("price"));

            HomeAppliance updatedAppliance = new HomeAppliance(id, sku, description, category, price);
            HomeApplianceDAO applianceDAO = new HomeApplianceDAO();
            boolean success = applianceDAO.updateItem(updatedAppliance);

            String response = String.format("""
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
                        <h1>%s</h1>
                        <p><a href='/adminPanel' class='btn'>Back to Admin Panel</a></p>
                    </div>
                </body>
                </html>
            """, success ? "Product Updated Successfully!" : "Failed to Update Product");

            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 400, """
                <html>
                <head>
                    <title>Home Solutions - Error</title>
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
                        <h1>Invalid Form Data or Server Error</h1>
                        <p><a href="/adminPanel" class="btn">Back to Admin Panel</a></p>
                    </div>
                </body>
                </html>
            """);
            e.printStackTrace();
        }
    }

    /**
     * Parses form-encoded data (application/x-www-form-urlencoded) into a Map of key-value pairs.
     *
     * @param formData The raw URL-encoded form data string from the request body.
     * @return A map containing the decoded key-value pairs.
     */
    private Map<String, String> parseFormData(String formData) {
        return Arrays.stream(formData.split("&"))
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(
                        pair -> URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        pair -> URLDecoder.decode(pair.length > 1 ? pair[1] : "", StandardCharsets.UTF_8)
                ));
    }

    /**
     * Sends an HTTP response with the specified status code and HTML body content.
     *
     * @param exchange    The HttpExchange object to send the response through.
     * @param statusCode  The HTTP status code (e.g., 200 for OK, 500 for error).
     * @param responseBody The HTML content to send as the response.
     * @throws IOException If an input or output error occurs during response transmission.
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}