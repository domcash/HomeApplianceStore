/**
 * The {@code LoginHandler} class handles user login requests, providing functionality for
 * displaying a login form, processing login credentials, and authenticating users against a
 * SQLite database. Successful login attempts set a session cookie, and failed attempts return
 * an appropriate error message.
 *
 * The handler supports both GET and POST methods:
 * GET: Displays the login form.
 * POST: Processes login credentials and validates them against the database.
 *
 * @author Dominic Cash
 */
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

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
                <title>Home Solutions - Admin Login</title>
                <meta charset='UTF-8'>
                <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>
                <style>
                    body {
                        font-family: 'Poppins', sans-serif;
                        background: linear-gradient(135deg, #f0f4f8, #d9e2ec);
                        margin: 0;
                        padding: 0;
                        color: #333;
                    }
                    .container {
                        max-width: 400px;
                        margin: 100px auto;
                        padding: 20px;
                        background: #fff;
                        border-radius: 10px;
                        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                        animation: fadeIn 1s ease-in;
                    }
                    @keyframes fadeIn {
                        from { opacity: 0; }
                        to { opacity: 1; }
                    }
                    h2 {
                        text-align: center;
                        color: #2c3e50;
                        font-size: 2em;
                        text-transform: uppercase;
                        letter-spacing: 2px;
                        margin-bottom: 20px;
                    }
                    input[type='text'], input[type='password'] {
                        width: 100%;
                        padding: 12px;
                        margin: 10px 0;
                        border-radius: 8px;
                        border: 1px solid #ddd;
                        font-size: 1em;
                        background: #fafafa;
                        box-sizing: border-box;
                        transition: border-color 0.3s;
                    }
                    input[type='text']:focus, input[type='password']:focus {
                        border-color: #3498db;
                        outline: none;
                    }
                    .btn {
                        background: #2ecc71;
                        color: white;
                        padding: 12px 25px;
                        text-align: center;
                        border: none;
                        cursor: pointer;
                        text-decoration: none;
                        display: block;
                        width: 100%;
                        border-radius: 25px;
                        font-weight: 600;
                        transition: transform 0.2s, background 0.3s;
                    }
                    .btn:hover {
                        background: #27ae60;
                        transform: translateY(-2px);
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Admin Login</h2>
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
                <head>
                    <title>Home Solutions - Login Success</title>
                    <meta charset='UTF-8'>
                    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>
                    <style>
                        body {
                            font-family: 'Poppins', sans-serif;
                            background: linear-gradient(135deg, #f0f4f8, #d9e2ec);
                            margin: 0;
                            padding: 0;
                            color: #333;
                        }
                        .container {
                            max-width: 600px;
                            margin: 100px auto;
                            padding: 20px;
                            background: #fff;
                            border-radius: 10px;
                            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                            animation: fadeIn 1s ease-in;
                            text-align: center;
                        }
                        @keyframes fadeIn {
                            from { opacity: 0; }
                            to { opacity: 1; }
                        }
                        h1 {
                            color: #2c3e50;
                            font-size: 2em;
                            text-transform: uppercase;
                            letter-spacing: 2px;
                            margin-bottom: 20px;
                        }
                        .btn {
                            background: #2ecc71;
                            color: white;
                            padding: 12px 25px;
                            text-align: center;
                            border: none;
                            cursor: pointer;
                            text-decoration: none;
                            display: inline-block;
                            border-radius: 25px;
                            font-weight: 600;
                            transition: transform 0.2s, background 0.3s;
                        }
                        .btn:hover {
                            background: #27ae60;
                            transform: translateY(-2px);
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Welcome, %s!</h1>
                        <p><a href="/adminPanel" class="btn">Go to Admin Panel</a></p>
                    </div>
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
                <head>
                    <title>Home Solutions - Login Failed</title>
                    <meta charset='UTF-8'>
                    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>
                    <style>
                        body {
                            font-family: 'Poppins', sans-serif;
                            background: linear-gradient(135deg, #f0f4f8, #d9e2ec);
                            margin: 0;
                            padding: 0;
                            color: #333;
                        }
                        .container {
                            max-width: 600px;
                            margin: 100px auto;
                            padding: 20px;
                            background: #fff;
                            border-radius: 10px;
                            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                            animation: fadeIn 1s ease-in;
                            text-align: center;
                        }
                        @keyframes fadeIn {
                            from { opacity: 0; }
                            to { opacity: 1; }
                        }
                        h1 {
                            color: #2c3e50;
                            font-size: 2em;
                            text-transform: uppercase;
                            letter-spacing: 2px;
                            margin-bottom: 20px;
                        }
                        .btn {
                            background: #2ecc71;
                            color: white;
                            padding: 12px 25px;
                            text-align: center;
                            border: none;
                            cursor: pointer;
                            text-decoration: none;
                            display: inline-block;
                            border-radius: 25px;
                            font-weight: 600;
                            transition: transform 0.2s, background 0.3s;
                        }
                        .btn:hover {
                            background: #27ae60;
                            transform: translateY(-2px);
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Invalid Username or Password</h1>
                        <p><a href="/login" class="btn">Back to Login</a></p>
                    </div>
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



