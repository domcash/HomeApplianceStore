import com.sun.net.httpserver.HttpExchange;	
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

//Dominic Cash
//16042439

/**
 * The {@code CustomerHandler} class handles HTTP requests to display a list of customers.
 * It retrieves customer data from the database using {@link CustomerDAO} and builds an
 * HTML response with the customer details formatted in a table.
 *
 * Features:
 * 
 * Fetches all customers from the database.
 * Generates an HTML table with customer details including ID, name, email, phone, and address.
 * Includes a "Back to Store" button for easy navigation.
 * 
 *
 * Example usage:
 * 
 * server.createContext("/customers", new CustomerHandler());
 * 
 *
 * Dependencies:
 * {@link CustomerDAO} for database operations.
 * {@link Customer} to represent customer data.
 * 
 *The response is styled with embedded CSS for a clean and responsive layout.
 *
 * @author Dominic Cash
 */
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
        response.append("<html>")
                .append("<head><title>Customers</title>")
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
                .append("<h1>Customer List</h1>")
                .append("<table>")
                .append("<thead>")
                .append("<tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Address</th></tr>") 
                .append("</thead>")
                .append("<tbody>");

        for (Customer customer : customers) {
            response.append("<tr>")
                    .append("<td>").append(customer.getCustomerID()).append("</td>")
                    .append("<td>").append(customer.getBusinessName()).append("</td>")
                    .append("<td>").append(customer.getEmail()).append("</td>")
                    .append("<td>").append(customer.getTelephoneNumber()).append("</td>")
                    .append("<td>").append(customer.getAddress()).append("</td>")
                    .append("</tr>");
        }

        response.append("</tbody>")
                .append("</table>")
                .append("<div class='footer'>")
                .append("<a href='/' class='btn'>Back to Store</a>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        byte[] responseBytes = response.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}