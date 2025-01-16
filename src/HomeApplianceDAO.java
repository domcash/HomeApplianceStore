import java.sql.Connection;	
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Dominic Cash
//16042439

/**
 * The HomeApplianceDAO class is responsible for interacting with the SQLite database to perform CRUD operations
 * on the appliance data. It provides methods to retrieve, insert, update, and delete home appliance records
 * from the database.
 *
 * @author dominic cash
 */
public class HomeApplianceDAO {

    private static final String URL = "jdbc:sqlite:stores.sqlite"; 

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return a Connection object to interact with the database, or null if the connection fails
     */
    protected Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Connected to database at: " + new java.io.File(URL.substring(12)).getAbsolutePath());
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    /**
     * Retrieves all products (appliances) from the database.
     *
     * @return a list of HomeAppliance objects containing details of all products in the database
     */
    public List<HomeAppliance> findAllProducts() {
        List<HomeAppliance> appliances = new ArrayList<>();
        String query = "SELECT * FROM appliance";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                HomeAppliance appliance = new HomeAppliance(
                        rs.getString("sku"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("price")
                );
                appliance.setId(rs.getInt("id"));
                appliances.add(appliance);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving products: " + e.getMessage());
        }

        return appliances;
    }

    /**
     * Retrieves all distinct categories from the appliance database.
     *
     * @return a list of unique categories
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT DISTINCT category FROM appliance";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving categories: " + e.getMessage());
        }

        return categories;
    }

    /**
     * Retrieves products (appliances) from the database based on the specified category.
     *
     * @param category the category to filter products by
     * @return a list of HomeAppliance objects in the specified category
     */
    public List<HomeAppliance> getProductsByCategory(String category) {
        List<HomeAppliance> appliances = new ArrayList<>();
        String query = "SELECT * FROM appliance WHERE category = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HomeAppliance appliance = new HomeAppliance(
                        rs.getString("sku"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("price")
                );
                appliance.setId(rs.getInt("id")); 
                appliances.add(appliance);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving products by category: " + e.getMessage());
        }

        return appliances;
    }

    /**
     * Retrieves products (appliances) from the database based on a search term in the description.
     *
     * @param searchTerm the term to search for in the product description
     * @return a list of HomeAppliance objects that match the search term in their description
     */
    public List<HomeAppliance> getProductsByDescription(String searchTerm) {
        String query = "SELECT * FROM appliance WHERE description LIKE ?";
        return executeProductQuery(query, "%" + searchTerm + "%");
    }

    /**
     * Executes a query to retrieve products from the database based on the provided SQL query and parameters.
     *
     * @param query the SQL query to execute
     * @param params the parameters to bind to the query
     * @return a list of HomeAppliance objects that match the query
     */
    private List<HomeAppliance> executeProductQuery(String query, String... params) {
        List<HomeAppliance> products = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(new HomeAppliance(
                        rs.getInt("id"),
                        rs.getString("sku"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Retrieves products (appliances) based on both category and price range.
     *
     * @param category   the category to filter products by
     * @param priceRange the price range to filter products by (e.g., "0-50", "51-100", etc.)
     * @return a list of HomeAppliance objects in the specified category and price range
     */
    public List<HomeAppliance> getProductsByCategoryAndPriceRange(String category, String priceRange) {
        List<HomeAppliance> appliances = new ArrayList<>();

        String query = "SELECT * FROM appliance WHERE category = ? AND " + buildPriceRangeQuery(priceRange);

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appliances.add(new HomeAppliance(
                            rs.getInt("id"),
                            rs.getString("sku"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getInt("price")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving products: " + e.getMessage());
        }

        return appliances;
    }

    /**
     * Retrieves products (appliances) based only on price range.
     *
     * @param priceRange the price range to filter products by (e.g., "0-50", "51-100", etc.)
     * @return a list of HomeAppliance objects in the specified price range
     */
    public List<HomeAppliance> getProductsByPriceRange(String priceRange) {
        List<HomeAppliance> appliances = new ArrayList<>();
        String query = "SELECT * FROM appliance ";

        String priceCondition = buildPriceRangeQuery(priceRange);

        query += "WHERE " + priceCondition;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HomeAppliance appliance = new HomeAppliance(
                        rs.getInt("id"),
                        rs.getString("sku"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("price")
                );
                appliances.add(appliance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appliances;
    }

    /**
     * Builds an SQL query string based on the provided price range.
     *
     * @param priceRange the price range to filter products by
     * @return the SQL fragment for filtering by price range
     */
    private String buildPriceRangeQuery(String priceRange) {
        Map<String, String> priceRanges = new HashMap<>();
        priceRanges.put("0-50", "price BETWEEN 0 AND 50");
        priceRanges.put("51-100", "price BETWEEN 51 AND 100");
        priceRanges.put("101-200", "price BETWEEN 101 AND 200");
        priceRanges.put("201-500", "price BETWEEN 201 AND 500");
        priceRanges.put("501-1000", "price BETWEEN 501 AND 1000");
        priceRanges.put("1001+", "price > 1000");

        return priceRanges.getOrDefault(priceRange, "price >= 0");
    }

    /**
     * Finds a product by its unique ID.
     *
     * @param id the ID of the product to find
     * @return a HomeAppliance object representing the product, or null if not found
     */
    public HomeAppliance findProduct(int id) {
        HomeAppliance appliance = null;
        String query = "SELECT * FROM appliance WHERE id = ?;";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                appliance = new HomeAppliance(
                        rs.getString("sku"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("price")
                );
                appliance.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println("Error finding product: " + e.getMessage());
        }

        return appliance;
    }

    /**
     * Inserts a new product into the database.
     *
     * @param appliance the HomeAppliance object to insert
     * @return true if the product was successfully inserted, false otherwise
     */
    public boolean insertItem(HomeAppliance appliance) {
        String query = "INSERT INTO appliance (sku, description, category, price) VALUES (?, ?, ?, ?);";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, appliance.getSku());
            stmt.setString(2, appliance.getDescription());
            stmt.setString(3, appliance.getCategory());
            stmt.setInt(4, appliance.getPrice());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error inserting product: " + e.getMessage());
        }

        return false;
    }

    /**
     * Updates an existing product in the database.
     *
     * @param appliance the HomeAppliance object with updated details
     * @return true if the product was successfully updated, false otherwise
     */
    public boolean updateItem(HomeAppliance appliance) {
        String query = "UPDATE appliance SET sku = ?, description = ?, category = ?, price = ? WHERE id = ?;";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, appliance.getSku());
            stmt.setString(2, appliance.getDescription());
            stmt.setString(3, appliance.getCategory());
            stmt.setInt(4, appliance.getPrice());
            stmt.setInt(5, appliance.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
        }

        return false;
    }

    /**
     * Deletes a product from the database based on its ID.
     *
     * @param id the ID of the product to delete
     * @return true if the product was successfully deleted, false otherwise
     */
    public boolean deleteItem(int id) {
        String query = "DELETE FROM appliance WHERE id = ?;";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
        
        return false;
    }
}






