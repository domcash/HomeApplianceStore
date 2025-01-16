import java.io.IOException;	
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;	
import com.sun.net.httpserver.HttpHandler;

//Dominic Cash
//16042439

/**
 * Handles HTTP requests related to the shopping basket in the application.
 * 
 * This class implements {@link HttpHandler} and provides functionality
 * to add items to the shopping basket, view the contents of the basket, and clear the basket.
 */
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
            response.append("<html>")
                    .append("<meta charset='UTF-8'>")
                    .append("<head><title>Shopping Basket</title></head>")
                    .append("<style>")
                    .append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }")
                    .append("h1 { text-align: center; color: #333; margin-top: 50px; }")
                    .append("table { width: 80%; margin: 20px auto; border-collapse: collapse; background-color: #fff; }")
                    .append("th, td { padding: 12px 20px; text-align: left; border: 1px solid #ddd; }")
                    .append("th { background-color: #4CAF50; color: white; font-size: 16px; }")
                    .append("tr:nth-child(even) { background-color: #f2f2f2; }")
                    .append("tr:hover { background-color: #ddd; }")
                    .append(".container { max-width: 1000px; margin: 0 auto; }")
                    .append(".footer { display: flex; justify-content: center; margin-top: 20px; }")
                    .append(".btn { background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; border: none; cursor: pointer; text-decoration: none; display: inline-block; border-radius: 5px; }")
                    .append(".btn:hover { background-color: #45a049; }")
                    .append("</style>")
                    .append("</head>")
                    .append("<body>")
                    .append("<a href='/appliances' class='btn' style='position: absolute; top: 20px; left: 20px;'>Back to Appliances</a>")
                    .append("<div class='container'>")
                    .append("<h1>Your Shopping Basket</h1>");

            response.append("<table>")
                    .append("<thead><tr><th>ID</th><th>SKU</th><th>Description</th><th>Category</th><th>Price</th></tr></thead><tbody>");

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
                    .append("<a href='/appliances' class='btn'>Continue Shopping</a>")
                    .append("<a href='/basket/clear' class='btn'>Clear Basket</a>")
                    .append("</div></body></html>");

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