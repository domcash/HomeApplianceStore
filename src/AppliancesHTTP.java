import com.sun.net.httpserver.HttpExchange;		
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

//Dominic Cash
//16042439

/**
 * Handles HTTP requests for displaying home appliances in a store.
 * This class implements the HttpHandler interface to process requests related to home appliances.
 * It supports filtering appliances by category, price range, and searching by description.
 * The response is generated dynamically based on the query parameters and includes an HTML page
 * with a list of appliances, filter options, and a search form.
 * @author dominic cash
 */
public class AppliancesHTTP implements HttpHandler {

    /**
     * Processes an HTTP request and generates an HTML response displaying a list of home appliances.
     * The list can be filtered by category, price range, or searched by description based on query parameters.
     * It also includes a filter form and a search bar for the user to refine the list of appliances.
     *
     * @param exchange The HTTP exchange representing the request and response.
     * @throws IOException If an input or output exception occurs during request handling.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = Controller.parseQueryParams(query);
        String category = params.getOrDefault("category", "").trim();
        String search = params.getOrDefault("search", "").trim();
        String priceRange = params.getOrDefault("priceRange", "").trim();

        HomeApplianceDAO applianceDAO = new HomeApplianceDAO();
        List<HomeAppliance> appliances;
        
        if (!category.isEmpty() && !priceRange.isEmpty()) {
            appliances = applianceDAO.getProductsByCategoryAndPriceRange(category, priceRange);
        } else if (!category.isEmpty()) {
            appliances = applianceDAO.getProductsByCategory(category);
        } else if (!priceRange.isEmpty()) {
            appliances = applianceDAO.getProductsByPriceRange(priceRange);
        } else if (!search.isEmpty()) {
            appliances = applianceDAO.getProductsByDescription(search);
        } else {
            appliances = applianceDAO.findAllProducts();
        }

        List<String> categories = applianceDAO.getAllCategories();

        StringBuilder response = new StringBuilder();
        response.append("<html>")
                .append("<head><title>Home Appliances</title>")
                .append("<meta charset='UTF-8'>")  
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
                .append("form { margin: 20px auto; display: flex; justify-content: center; gap: 10px; }")
                .append("select, input[type='text'] { padding: 10px; border-radius: 5px; }")
                .append("button { background-color: #4CAF50; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; }")
                .append("button:hover { background-color: #45a049; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<a href='/login' class='btn' style='position: absolute; top: 20px; left: 20px;'>Admin Login</a>")
                .append("<a href='/customers' class='btn' style='position: absolute; top: 20px; right: 20px;'>View Customers</a>")
                .append("<div class='container'>")
                .append("<h1>Welcome to the Home Appliance Store</h1>");

        response.append("<form method='GET' action='/appliances'>")
                .append("<label for='category'>Filter by Category:</label>")
                .append("<select name='category'>")
                .append("<option value=''>All</option>");

        for (String cat : categories) {
            boolean isSelected = category.equals(cat);
            response.append("<option value='").append(cat).append("'")
                    .append(isSelected ? " selected" : "")
                    .append(">")
                    .append(cat)
                    .append("</option>");
        }

        response.append("</select>");

        response.append("<label for='priceRange'>Price Range:</label>")
                .append("<select name='priceRange'>")
                .append("<option value=''>All</option>")
                .append("<option value='0-50'").append(priceRange.equals("0-50") ? " selected" : "").append(">£0 - £50</option>")
                .append("<option value='51-100'").append(priceRange.equals("51-100") ? " selected" : "").append(">£51 - £100</option>")
                .append("<option value='101-200'").append(priceRange.equals("101-200") ? " selected" : "").append(">£101 - £200</option>")
                .append("<option value='201-500'").append(priceRange.equals("201-500") ? " selected" : "").append(">£201 - £500</option>")
                .append("<option value='501-1000'").append(priceRange.equals("501-1000") ? " selected" : "").append(">£501 - £1000</option>")
                .append("<option value='1001+'").append(priceRange.equals("1001+") ? " selected" : "").append(">£1001+</option>")
                .append("</select>");

        response.append("<button type='submit'>Filter</button>")
                .append("</form>");

        response.append("<form method='GET' action='/appliances'>")
                .append("<input type='text' name='search' placeholder='Search by description' required />")
                .append("<button type='submit'>Search</button>")
                .append("</form>");

        response.append("<table>")
                .append("<thead><tr><th>ID</th><th>SKU</th><th>Description</th><th>Category</th><th>Price</th></tr></thead><tbody>");

        for (HomeAppliance appliance : appliances) {
            response.append("<tr>")
                    .append("<td>").append(appliance.getId()).append("</td>")
                    .append("<td>").append(appliance.getSku()).append("</td>")
                    .append("<td>").append(appliance.getDescription()).append("</td>")
                    .append("<td>").append(appliance.getCategory()).append("</td>")
                    .append("<td>£").append(appliance.getPrice()).append("</td>")
                    .append("<td><a href='/basket/add?id=").append(appliance.getId()).append("' class='btn'>Add to Basket</a></td>")
                    .append("</tr>");
        }

        response.append("</tbody></table>")
                .append("<div class='footer'>")
                .append("<a href='/appliances' class='btn'>Back to Store</a>")
                .append("<a href='/basket/view' class='btn'>View Basket</a>") 
                .append("</div>")
                .append("</div>")
                .append("</body></html>");

        byte[] responseBytes = response.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}