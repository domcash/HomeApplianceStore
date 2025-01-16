import com.sun.net.httpserver.HttpHandler;	
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

//Dominic Cash
//16042439

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
 * @author dominic cash
 */
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
            response.append("<html>")
                    .append("<head><title>Edit Product</title></head>")
                    .append("<body>")
                    .append("<h1>Edit Product</h1>")
                    .append("<form method='POST' action='/editProduct'>")
                    .append("<input type='hidden' name='id' value='").append(appliance.getId()).append("' />")
                    .append("SKU: <input type='text' name='sku' value='").append(appliance.getSku()).append("' required /><br>")
                    .append("Description: <input type='text' name='description' value='").append(appliance.getDescription()).append("' required /><br>")
                    .append("Category: <input type='text' name='category' value='").append(appliance.getCategory()).append("' required /><br>")
                    .append("Price: <input type='number' name='price' value='").append(appliance.getPrice()).append("' required /><br>")
                    .append("<button type='submit'>Update Product</button>")
                    .append("</form>")
                    .append("</body>")
                    .append("</html>");

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
            response.append("<html>")
                    .append("<head><title>Product Update</title></head>")
                    .append("<body>");

            if (success) {
                response.append("<h1>Product updated successfully!</h1>");
            } else {
                response.append("<h1>Failed to update the product. Please try again.</h1>");
            }
                    response.append("<a href='/products'>View All Products</a>")
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