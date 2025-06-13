/**
 * Handles HTTP requests for displaying home appliances in a store.
 * This class implements the HttpHandler interface to process requests related to home appliances.
 * It supports filtering appliances by category, price range, and searching by description.
 * The response is generated dynamically based on the query parameters and includes an HTML page
 * with a list of appliances, filter options, and a search form.
 * @author dominic cash
 */
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AppliancesHTTP implements HttpHandler {

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

        // Header and CSS (all buttons use the same green styling)
        response.append("<html>\n" +
                "<head>\n" +
                "<title>Home Solutions</title>\n" +
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
                "form {\n" +
                "    margin: 30px auto;\n" +
                "    display: flex;\n" +
                "    justify-content: center;\n" +
                "    gap: 15px;\n" +
                "    background: #fff;\n" +
                "    padding: 20px;\n" +
                "    border-radius: 10px;\n" +
                "    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);\n" +
                "}\n" +
                "select, input[type='text'] {\n" +
                "    padding: 12px;\n" +
                "    border-radius: 8px;\n" +
                "    border: 1px solid #ddd;\n" +
                "    font-size: 1em;\n" +
                "    background: #fafafa;\n" +
                "    transition: border-color 0.3s;\n" +
                "}\n" +
                "select:focus, input[type='text']:focus {\n" +
                "    border-color: #3498db;\n" +
                "    outline: none;\n" +
                "}\n" +
                "button {\n" +
                "    background: #2ecc71;\n" +
                "    color: white;\n" +
                "    border: none;\n" +
                "    padding: 12px 25px;\n" +
                "    border-radius: 8px;\n" +
                "    cursor: pointer;\n" +
                "    font-weight: 600;\n" +
                "    transition: background 0.3s;\n" +
                "}\n" +
                "button:hover {\n" +
                "    background: #27ae60;\n" +
                "}\n" +
                ".footer {\n" +
                "    display: flex;\n" +
                "    justify-content: center;\n" +
                "    gap: 20px;\n" +
                "    margin-top: 40px;\n" +
                "}\n" +
                ".admin-login {\n" +  // Keep positioning, no color override
                "    position: absolute;\n" +
                "    top: 20px;\n" +
                "    left: 20px;\n" +
                "}\n" +
                ".view-customers {\n" +  // Keep positioning, no color override
                "    position: absolute;\n" +
                "    top: 20px;\n" +
                "    right: 20px;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n");

        // Navigation buttons
        response.append("<a href='/login' class='btn admin-login'>Admin Login</a>")
                .append("<a href='/customers' class='btn view-customers'>View Customers</a>")
                .append("<div class='container'>")
                .append("<h1>Welcome to Home Solutions</h1>");

        // Filter form
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

        response.append("</select>")
                .append("<label for='priceRange'>Price Range:</label>")
                .append("<select name='priceRange'>")
                .append("<option value=''>All</option>")
                .append("<option value='0-50'").append(priceRange.equals("0-50") ? " selected" : "").append(">£0 - £50</option>")
                .append("<option value='51-100'").append(priceRange.equals("51-100") ? " selected" : "").append(">£50 - £100</option>")
                .append("<option value='101-200'").append(priceRange.equals("101-200") ? " selected" : "").append(">£100 - £200</option>")
                .append("<option value='201-500'").append(priceRange.equals("201-500") ? " selected" : "").append(">£200 - £500</option>")
                .append("<option value='501-1000'").append(priceRange.equals("501-1000") ? " selected" : "").append(">£500 - £1000</option>")
                .append("<option value='1001+'").append(priceRange.equals("1001+") ? " selected" : "").append(">£1000+</option>")
                .append("</select>")
                .append("<button type='submit'>Filter</button>")
                .append("</form>");

        // Search form
        response.append("<form method='GET' action='/appliances'>")
                .append("<input type='text' name='search' placeholder='Search by description' required />")
                .append("<button type='submit'>Search</button>")
                .append("</form>");

        // Appliance table
        response.append("<table>")
                .append("<thead><tr><th>ID</th><th>SKU</th><th>Description</th><th>Category</th><th>Price</th><th>Action</th></tr></thead>")
                .append("<tbody>");

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

        response.append("</tbody></table>");

        // Footer buttons
        response.append("<div class='footer'>")
                .append("<a href='/appliances' class='btn'>Back to Store</a>")
                .append("<a href='/basket/view' class='btn'>View Basket</a>")
                .append("</div>")
                .append("</div>")
                .append("</body></html>");

        // Send the response
        byte[] responseBytes = response.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}