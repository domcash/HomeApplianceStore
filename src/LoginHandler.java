import com.sun.net.httpserver.HttpExchange;	
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

//Dominic Cash
//16042439

/**
 * The {@code LoginHandler} class handles user login requests, providing functionality for
 * displaying a login form, processing login credentials, and authenticating users against a
 * SQLite database. Successful login attempts set a session cookie, and failed attempts return
 * an appropriate error message.
 * 
 * The handler supports both GET and POST methods:
 * 
 * GET: Displays the login form.
 * POST: Processes login credentials and validates them against the database.
 * 
 *
 * Example usage:
 * 
 * LoginHandler handler = new LoginHandler();
 * handler.handle(exchange);
 * 
 * 
 *
 * @author Dominic Cash
 */
public class LoginHandler implements HttpHandler {

    /**
     * Handles the incoming HTTP requests for the /login endpoint.
     * Supports GET requests to display the login form and POST requests to process login data.
     *
     * @param exchange the {@link HttpExchange} object that represents the HTTP request and response
     * @throws IOException if an I/O error occurs during the process of handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            displayLoginForm(exchange);
        } else if ("POST".equalsIgnoreCase(method)) {
            processLogin(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1); 
        }
    }

    /**
     * Displays an HTML login form when the user navigates to the /login endpoint.
     *
     * @param exchange the {@link HttpExchange} object representing the HTTP request and response
     * @throws IOException if an I/O error occurs during response generation
     */
    private void displayLoginForm(HttpExchange exchange) throws IOException {
        String response = """
            <html>
            <head>
                <title>Admin Login</title>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 400px; margin: 100px auto; padding: 20px; background-color: #f4f4f4; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }
                    input { width: 100%; padding: 10px; margin: 10px 0; }
                    .btn { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; cursor: pointer; width: 100%; }
                    .btn:hover { background-color: #45a049; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Login</h2>
                    <form action="/login" method="POST">
                        <input type="text" name="username" placeholder="Username" required />
                        <input type="password" name="password" placeholder="Password" required />
                        <button type="submit" class="btn">Login</button>
                    </form>
                </div>
            </body>
            </html>
        """;

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Processes the login form submission, authenticating the user's credentials
     * against the SQLite database. If the login is successful, a session cookie is set.
     *
     * @param exchange the {@link HttpExchange} object representing the HTTP request and response
     * @throws IOException if an I/O error occurs during response generation
     */
    private void processLogin(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String formData = reader.readLine();

        Map<String, String> params = parseQueryParams(formData);
        String username = params.get("username");
        String password = params.get("password");

        boolean isAuthenticated = authenticateUser(username, password);

        if (isAuthenticated) {
            String sessionCookie = "session=" + username + "; Path=/; HttpOnly"; 
            exchange.getResponseHeaders().set("Set-Cookie", sessionCookie);

            String response = """
                <html>
                <head><title>Login Success</title></head>
                <body>
                    <h1>Welcome, %s!</h1>
                    <p><a href="/adminPanel" >Go to Admin Panel</a></p>
                </body>
                </html>
            """.formatted(username);

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            String response = """
                <html>
                <head><title>Login Failed</title></head>
                <body>
                    <h1>Invalid Username or Password</h1>
                    <p><a href="/login">Back to Login</a></p>
                </body>
                </html>
            """;

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(401, response.getBytes(StandardCharsets.UTF_8).length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * Parses query parameters from the form data submitted in a POST request.
     *
     * @param formData the raw query string containing key-value pairs
     * @return a map of parsed query parameters
     */
    private Map<String, String> parseQueryParams(String formData) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            try {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return params;
    }

    /**
     * Authenticates a user's credentials by comparing the provided password
     * against the hashed password stored in the SQLite database.
     *
     * @param username the username of the user
     * @param password the plain text password to be authenticated
     * @return {@code true} if the credentials are valid, {@code false} otherwise
     */
    private boolean authenticateUser(String username, String password) {
        String dbUrl = "jdbc:sqlite:stores.sqlite";
        String query = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                return BCrypt.checkpw(password, storedHash); 
            } else {
                return false; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}



