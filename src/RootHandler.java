import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

//Dominic Cash
//16042439

/**
 * The {@code RootHandler} class is responsible for setting up an HTTP server and handling
 * various routes for displaying products, customers, admin panel, and managing user login/logout.
 * <p>
 * It creates the server on port 8080 and binds different contexts to specific URL paths, including:
 * <p>
 * Root context ("/") to display products.
 * "/customers" to display customer information.
 * "/adminPanel" for the admin panel.
 * "/addProduct", "/editProduct", and "/deleteProduct" for product management.
 * "/login" and "/logout" for user authentication.
 * <p>
 * Example usage:
 * <p>
 * RootHandler.main(new String[] {});
 *
 * @author Dominic Cash
 */
public class RootHandler {

    private static final int PORT = 8080;

    /**
     * Main method that initializes the HTTP server and registers the context handlers.
     * It starts the server on port 8080 and binds various request handlers to specific URL paths.
     *
     * @param args commandline arguments (not used)
     * @throws IOException if an I/O error occurs when creating or starting the server
     */
    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new AppliancesHTTP());

        server.createContext("/customers", new CustomerHandler());

        server.createContext("/adminPanel", new AdminPanelHandler());

        server.createContext("/addProduct", new AddProductHandler());

        server.createContext("/editProduct", new EditProductHandler());

        server.createContext("/deleteProduct", new DeleteProductHandler());

        server.createContext("/login", new LoginHandler());

        server.createContext("/logout", new LogoutHandler());

        ShoppingBasket basket = new ShoppingBasket();

        HomeApplianceDAO applianceDAO = new HomeApplianceDAO();

        server.createContext("/basket/add", new BasketHandler(basket, applianceDAO));

        server.createContext("/basket/view", new BasketHandler(basket, applianceDAO));

        server.createContext("/basket/clear", new BasketHandler(basket, applianceDAO));


        server.start();
        System.out.println("Server started on port " + PORT);
    }
}




