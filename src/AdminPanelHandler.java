/**
 * Handles HTTP requests for the admin panel, providing a page for managing home appliances.
 * This class verifies whether the user is logged in, displays a table of appliances, and offers options
 * to add, edit, or delete products from the store.
 *
 * It interacts with the HomeApplianceDAO to fetch appliance data and renders an HTML page
 * displaying a list of appliances and actions to manage them.
 *
 * @see HttpHandler
 * @author Dominic Cash
 */
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        response.append("<html>\n" +
                "<head>\n" +
                "<title>Home Solutions - Admin Panel</title>\n" +  // Updated title
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
                "h2 {\n" +
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
                "    margin: 5px;\n" +  // Added margin for spacing between Edit/Delete buttons
                "}\n" +
                ".btn:hover {\n" +
                "    background: #27ae60;\n" +
                "    transform: translateY(-2px);\n" +
                "}\n" +
                ".add-new {\n" +
                "    display: block;\n" +
                "    width: 200px;\n" +
                "    margin: 20px auto;\n" +
                "    text-align: center;\n" +
                "}\n" +
                ".logout {\n" +
                "    position: absolute;\n" +
                "    top: 20px;\n" +
                "    left: 20px;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n");

        // Logout button
        response.append("<a href='/' class='btn logout'>Logout</a>")
                .append("<div class='container'>")
                .append("<h2>Admin Panel - Manage Appliances</h2>")
                .append("<a href='/addProduct' class='btn add-new'>Add New Appliance</a>")
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
                    .append("<a href='/deleteProduct?id=").append(appliance.getId()).append("' class='btn'>Delete</a>")
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