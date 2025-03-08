/**
 * Handles HTTP requests related to the shopping basket in the application.
 *
 * This class implements {@link HttpHandler} and provides functionality
 * to add items to the shopping basket, view the contents of the basket, and clear the basket.
 * @author Dominic Cash
 */
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class BasketHandler implements HttpHandler {

    private ShoppingBasket basket;
    private HomeApplianceDAO applianceDAO;

    /**
     * Creates a new instance of BasketHandler with the specified shopping basket and appliance DAO.
     *
     * @param basket the shopping basket to manage
     * @param applianceDAO the data access object for retrieving appliance details
     */
    public BasketHandler(ShoppingBasket basket, HomeApplianceDAO applianceDAO) {
        this.basket = basket;
        this.applianceDAO = applianceDAO;
    }

    /**
     * Handles HTTP requests to manage the shopping basket.
     *
     * Processes requests to:
     * Add an appliance to the shopping basket
     * Display the current contents of the basket
     * Clear all items from the basket
     *
     * @param exchange the HTTP exchange object representing the request and response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/basket/add")) {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = Controller.parseQueryParams(query);
            String id = params.get("id");

            if (id != null) {
                HomeAppliance appliance = applianceDAO.findProduct(Integer.parseInt(id));
                if (appliance != null) {
                    basket.addItem(appliance);
                }
            }
            exchange.getResponseHeaders().set("Location", "/appliances");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        if (path.equals("/basket/view")) {
            StringBuilder response = new StringBuilder();
            response.append("<html>\n" +
                    "<head>\n" +
                    "<title>Home Solutions - Shopping Basket</title>\n" +  // Updated title
                    "<meta charset='UTF-8'>\n" +
                    "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>\n" +
                    "<style>\n" +
                    "body {\n" +
                    "    font-family: 'Poppins', sans-serif;\n" +
                    "    background: linear-gradient(135deg, #f0f4f8, #d9e2ec);\n" +
                    "    margin: 0;\n" +
                    "    padding: 0;\n" +
                    "    color: #333;\n" +
                    "}\n" +
                    "h1 {\n" +
                    "    text-align: center;\n" +
                    "    color: #2c3e50;\n" +
                    "    margin-top: 60px;\n" +
                    "    font-size: 2.5em;\n" +
                    "    text-transform: uppercase;\n" +
                    "    letter-spacing: 2px;\n" +
                    "}\n" +
                    "h2 {\n" +
                    "    text-align: center;\n" +
                    "    color: #2c3e50;\n" +
                    "    font-size: 1.5em;\n" +
                    "    margin: 20px 0;\n" +
                    "}\n" +
                    ".container {\n" +
                    "    max-width: 1200px;\n" +
                    "    margin: 0 auto;\n" +
                    "    padding: 20px;\n" +
                    "    animation: fadeIn 1s ease-in;\n" +
                    "}\n" +
                    "@keyframes fadeIn {\n" +
                    "    from { opacity: 0; }\n" +
                    "    to { opacity: 1; }\n" +
                    "}\n" +
                    "table {\n" +
                    "    width: 100%;\n" +
                    "    margin: 30px 0;\n" +
                    "    border-collapse: collapse;\n" +
                    "    background: #fff;\n" +
                    "    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);\n" +
                    "    border-radius: 10px;\n" +
                    "    overflow: hidden;\n" +
                    "}\n" +
                    "th, td {\n" +
                    "    padding: 15px 20px;\n" +
                    "    text-align: left;\n" +
                    "    border-bottom: 1px solid #eee;\n" +
                    "}\n" +
                    "th {\n" +
                    "    background: #3498db;\n" +
                    "    color: white;\n" +
                    "    font-size: 1.1em;\n" +
                    "    text-transform: uppercase;\n" +
                    "    letter-spacing: 1px;\n" +
                    "}\n" +
                    "tr:nth-child(even) {\n" +
                    "    background-color: #f9fbfc;\n" +
                    "}\n" +
                    "tr:hover {\n" +
                    "    background-color: #ecf0f1;\n" +
                    "    transition: background-color 0.3s ease;\n" +
                    "}\n" +
                    ".btn {\n" +
                    "    background: #2ecc71;\n" +
                    "    color: white;\n" +
                    "    padding: 12px 25px;\n" +
                    "    text-align: center;\n" +
                    "    border: none;\n" +
                    "    cursor: pointer;\n" +
                    "    text-decoration: none;\n" +
                    "    display: inline-block;\n" +
                    "    border-radius: 25px;\n" +
                    "    font-weight: 600;\n" +
                    "    transition: transform 0.2s, background 0.3s;\n" +
                    "}\n" +
                    ".btn:hover {\n" +
                    "    background: #27ae60;\n" +
                    "    transform: translateY(-2px);\n" +
                    "}\n" +
                    ".footer {\n" +
                    "    display: flex;\n" +
                    "    justify-content: center;\n" +
                    "    gap: 20px;\n" +
                    "    margin-top: 40px;\n" +
                    "}\n" +
                    ".back-to-appliances {\n" +
                    "    position: absolute;\n" +
                    "    top: 20px;\n" +
                    "    left: 20px;\n" +
                    "}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n");

            // Back to Appliances button
            response.append("<a href='/appliances' class='btn back-to-appliances'>Back to Appliances</a>")
                    .append("<div class='container'>")
                    .append("<h1>Your Shopping Basket</h1>");

            // Basket table
            response.append("<table>")
                    .append("<thead><tr><th>ID</th><th>SKU</th><th>Description</th><th>Category</th><th>Price</th></tr></thead>")
                    .append("<tbody>");

            for (HomeAppliance item : basket.getItems()) {
                response.append("<tr>")
                        .append("<td>").append(item.getId()).append("</td>")
                        .append("<td>").append(item.getSku()).append("</td>")
                        .append("<td>").append(item.getDescription()).append("</td>")
                        .append("<td>").append(item.getCategory()).append("</td>")
                        .append("<td>£").append(item.getPrice()).append("</td>")
                        .append("</tr>");
            }

            response.append("</tbody></table>")
                    .append("<h2>Total: £").append((int) basket.getTotalPrice()).append("</h2>")
                    .append("<div class='footer'>")
                    .append("<a href='/appliances' class='btn'>Continue Shopping</a>")
                    .append("<a href='/basket/clear' class='btn'>Clear Basket</a>")
                    .append("</div>")
                    .append("</div>")
                    .append("</body></html>");

            byte[] responseBytes = response.toString().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }

        if (path.equals("/basket/clear")) {
            basket.clearBasket();
            exchange.getResponseHeaders().set("Location", "/basket/view");
            exchange.sendResponseHeaders(302, -1);
        }
    }
}