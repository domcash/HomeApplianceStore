import com.sun.net.httpserver.HttpExchange;	
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

//Dominic Cash
//16042439

/**
 * The {@code LogoutHandler} class handles user logout requests.
 * When a user sends a request to log out, this handler clears the session (or performs
 * any necessary logout actions) and sends a response confirming the logout.
 * The response includes a link to redirect the user to the login page.
 * The handler responds with a simple HTML message confirming the logout action,
 * with a link for the user to log in again.
 *
 * Example usage:
 * LogoutHandler handler = new LogoutHandler();
 * handler.handle(exchange);
 * 
 *
 * @author Dominic Cash
 */
public class LogoutHandler implements HttpHandler {

    /**
     * Handles the logout request. This method clears the session (or performs any other necessary
     * actions) and sends an HTML response confirming that the user has been logged out.
     * The response includes a link to the login page.
     *
     * @param exchange the {@link HttpExchange} object that represents the HTTP request and response
     * @throws IOException if an I/O error occurs during the process of sending the response
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "<html><body>Logged out successfully! <a href='/login'>Login again</a></body></html>";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}