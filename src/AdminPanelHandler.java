import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
//Dominic Cash
//16042439

/**
 * Handles HTTP requests for the admin panel, providing a page for managing home appliances.
 * This class verifies whether the user is logged in, displays a table of appliances, and offers options
 * to add, edit, or delete products from the store.
 *
 * It interacts with the HomeApplianceDAO to fetch appliance data and renders an HTML page
 * displaying a list of appliances and actions to manage them.
 *
 * @see HttpHandler
 */
public class AdminPanelHandler implements HttpHandler {

    private HomeApplianceDAO applianceDAO = new HomeApplianceDAO(); // Initialize the DAO

    /**
     * Handles HTTP requests by generating an HTML response that displays the admin panel with a list of appliances.
     * If the user is not logged in, redirects to the login page.
     *
     * @param exchange The HTTP exchange containing the request and response information.
     * @throws IOException If an input or output exception occurs during processing the request.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean isLoggedIn = checkLoginStatus(exchange);
        if (!isLoggedIn) {
            exchange.getResponseHeaders().set("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        List<HomeAppliance> appliances = applianceDAO.findAllProducts();
        StringBuilder response = new StringBuilder();
        response.append("<html>")
                .append("<head><title>Admin Panel</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; }")
                .append(".container { max-width: 1000px; margin: 50px auto; padding: 20px; background-color: #f4f4f4; border-radius: 5px; }")
                .append("h2 { text-align: center; color: #333; }")
                .append("table { width: 100%; margin-top: 20px; border-collapse: collapse; background-color: #fff; }")
                .append("th, td { padding: 12px 20px; text-align: left; border: 1px solid #ddd; }")
                .append("th { background-color: #4CAF50; color: white; font-size: 16px; }")
                .append("tr:nth-child(even) { background-color: #f2f2f2; }")
                .append("tr:hover { background-color: #ddd; }")
                .append(".btn { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; cursor: pointer; text-decoration: none; }")
                .append(".btn:hover { background-color: #45a049; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                // Add the "Logout" button in the top left corner
                .append("<a href='/' class='btn' style='position: absolute; top: 20px; left: 20px;'>Logout</a>")
                .append("<div class='container'>")
                .append("<h2>Admin Panel - Manage Appliances</h2>")
                .append("<p style='text-align: center;'>")
                .append("<a href='/addProduct' class='btn'>Add New Appliance</a>")
                .append("</p>")
                .append("<table>")
                .append("<thead>")
                .append("<tr><th>ID</th><th>SKU</th><th>Description</th><th>Category</th><th>Price</th><th>Actions</th></tr>")
                .append("</thead>")
                .append("<tbody>");

        for (HomeAppliance appliance : appliances) {
            response.append("<tr>")
                    .append("<td>").append(appliance.getId()).append("</td>")
                    .append("<td>").append(appliance.getSku()).append("</td>")
                    .append("<td>").append(appliance.getDescription()).append("</td>")
                    .append("<td>").append(appliance.getCategory()).append("</td>")
                    .append("<td>£").append(appliance.getPrice()).append("</td>")
                    .append("<td>")
                    .append("<a href='/editProduct?id=").append(appliance.getId()).append("' class='btn'>Edit</a>")
                    .append(" <a href='/deleteProduct?id=").append(appliance.getId()).append("' class='btn'>Delete</a>")
                    .append("</td>")
                    .append("</tr>");
        }

        response.append("</tbody>")
                .append("</table>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

        byte[] responseBytes = response.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    /**
     * Checks whether the user is logged in by inspecting the session cookie.
     *
     * @param exchange The HTTP exchange containing the request headers.
     * @return true if the user is logged in, false otherwise.
     */
    private boolean checkLoginStatus(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null && cookieHeader.contains("session=")) {
            return true; 
        }
        return false; 
    }
}