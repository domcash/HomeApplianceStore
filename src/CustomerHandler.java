/**
 * The {@code CustomerHandler} class handles HTTP requests to display a list of customers.
 * It retrieves customer data from the database using {@link CustomerDAO} and builds an
 * HTML response with the customer details formatted in a table.
 *
 * Features:
 * Fetches all customers from the database.
 * Generates an HTML table with customer details including ID, name, email, phone, and address.
 * Includes a "Back to Store" button for easy navigation.
 *
 * Example usage:
 * server.createContext("/customers", new CustomerHandler());
 *
 * Dependencies:
 * {@link CustomerDAO} for database operations.
 * {@link Customer} to represent customer data.
 *
 * The response is styled with embedded CSS for a clean and responsive layout.
 * @author Dominic Cash
 */
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CustomerHandler implements HttpHandler {

    /**
     * Handles incoming HTTP requests to the /customers endpoint.
     * Generates and sends an HTML response with a table of customer details.
     *
     * @param exchange the {@link HttpExchange} object containing the request and response
     * @throws IOException if an I/O error occurs during processing
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customers = customerDAO.findAllCustomers();

        StringBuilder response = new StringBuilder();
        response.append("<html>\n" +
                "<head>\n" +
                "<title>Home Solutions - Customers</title>\n" +  // Updated title
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
                ".footer {\n" +
                "    display: flex;\n" +
                "    justify-content: center;\n" +
                "    gap: 20px;\n" +
                "    margin-top: 40px;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class='container'>\n" +
                "<h1>Customer List</h1>\n" +
                "<table>\n" +
                "<thead>\n" +
                "<tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Address</th></tr>\n" +
                "</thead>\n" +
                "<tbody>");

        for (Customer customer : customers) {
            response.append("<tr>")
                    .append("<td>").append(customer.getCustomerID()).append("</td>")
                    .append("<td>").append(customer.getBusinessName()).append("</td>")
                    .append("<td>").append(customer.getEmail()).append("</td>")
                    .append("<td>").append(customer.getTelephoneNumber()).append("</td>")
                    .append("<td>").append(customer.getAddress()).append("</td>")
                    .append("</tr>");
        }

        response.append("</tbody>\n" +
                "</table>\n" +
                "<div class='footer'>\n" +
                "<a href='/' class='btn'>Back to Store</a>\n" +
                "</div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>");

        byte[] responseBytes = response.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}