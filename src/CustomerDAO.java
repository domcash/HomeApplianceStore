import java.sql.Connection;	
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//Dominic Cash
//16042439

/**
 * The {@code CustomerDAO} class provides CRUD (Create, Read, Update, Delete) operations
 * for the {@link Customer} entity in the database. It handles interactions with the
 * "customer" table in the SQLite database.
 *
 * Features:
 * 
 * Retrieve all customers
 * Find a specific customer by ID
 * Insert a new customer
 * Update an existing customer
 * Delete a customer
 * 
 *
 * The database connection uses the SQLite driver and the file "stores.sqlite".
 *
 * @author Dominic Cash
 */
public class CustomerDAO {

    /** The URL for the SQLite database connection. */
    private static final String URL = "jdbc:sqlite:stores.sqlite";

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return a {@link Connection} object to the database, or {@code null} if the connection fails
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
     * Retrieves all customers from the database.
     *
     * @return a {@link List} of {@link Customer} objects representing all customers in the database
     */
    public List<Customer> findAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customer";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Address address = new Address(
                        rs.getString("addressLine0"),
                        rs.getString("addressLine1"),
                        rs.getString("addressLine2"),
                        rs.getString("country"),
                        rs.getString("postCode")
                );
                Customer customer = new Customer(
                        rs.getInt("customerID"),
                        rs.getString("businessName"),
                        address,
                        rs.getString("telephoneNumber"),
                        rs.getString("email") 
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customers: " + e.getMessage());
        }

        return customers;
    }

    /**
     * Finds a specific customer by their ID.
     *
     * @param customerId the ID of the customer to find
     * @return a {@link Customer} object representing the customer, or {@code null} if not found
     */
    public Customer findCustomer(int customerId) {
        Customer customer = null;
        String query = "SELECT * FROM customer WHERE customerID = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Address address = new Address(
                        rs.getString("addressLine0"),
                        rs.getString("addressLine1"),
                        rs.getString("addressLine2"),
                        rs.getString("country"),
                        rs.getString("postCode")
                );
                customer = new Customer(
                        rs.getInt("customerID"),
                        rs.getString("businessName"),
                        address,
                        rs.getString("telephoneNumber"),
                        rs.getString("email") 
                );
            }
        } catch (SQLException e) {
            System.out.println("Error finding customer: " + e.getMessage());
        }

        return customer;
    }

    /**
     * Inserts a new customer into the database.
     *
     * @param customer the {@link Customer} object to insert
     * @return {@code true} if the customer was inserted successfully, {@code false} otherwise
     */
    public boolean insertCustomer(Customer customer) {
        String query = "INSERT INTO customer (businessName, addressLine0, addressLine1, addressLine2, country, postCode, telephoneNumber, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getBusinessName());
            stmt.setString(2, customer.getAddress().getAddressLine0());
            stmt.setString(3, customer.getAddress().getAddressLine1());
            stmt.setString(4, customer.getAddress().getAddressLine2());
            stmt.setString(5, customer.getAddress().getCountry());
            stmt.setString(6, customer.getAddress().getPostCode());
            stmt.setString(7, customer.getTelephoneNumber());
            stmt.setString(8, customer.getEmail()); 

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error inserting customer: " + e.getMessage());
        }

        return false;
    }

    /**
     * Updates an existing customer in the database.
     *
     * @param customer the {@link Customer} object with updated information
     * @return {@code true} if the customer was updated successfully, {@code false} otherwise
     */
    public boolean updateCustomer(Customer customer) {
        String query = "UPDATE customer SET businessName = ?, addressLine0 = ?, addressLine1 = ?, addressLine2 = ?, country = ?, postCode = ?, telephoneNumber = ?, email = ? WHERE customerID = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getBusinessName());
            stmt.setString(2, customer.getAddress().getAddressLine0());
            stmt.setString(3, customer.getAddress().getAddressLine1());
            stmt.setString(4, customer.getAddress().getAddressLine2());
            stmt.setString(5, customer.getAddress().getCountry());
            stmt.setString(6, customer.getAddress().getPostCode());
            stmt.setString(7, customer.getTelephoneNumber());
            stmt.setString(8, customer.getEmail()); 
            stmt.setInt(9, customer.getCustomerID());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
        }

        return false;
    }

    /**
     * Deletes a customer from the database by their ID.
     *
     * @param customerId the ID of the customer to delete
     * @return {@code true} if the customer was deleted successfully, {@code false} otherwise
     */
    public boolean deleteCustomer(int customerId) {
        String query = "DELETE FROM customer WHERE customerID = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
        }

        return false;
    }
}

