import java.sql.Connection;	
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

//Dominic Cash
//16042439

/**
 * The {@code UserInserter} class provides functionality for inserting a new user into the
 * database with a hashed password.
 *
 * The password is hashed using the BCrypt hashing algorithm before being stored in the database.
 * The class connects to a SQLite database to execute an insert query to store the username
 * and hashed password in the users table.
 *
 * Example usage:
 * UserInserter.insertUser("username", "plainPassword");
 * 
 * @author Dominic Cash
 */
public class UserInserter {

    /**
     * Default constructor for the {@code UserInserter} class.
     * This constructor does not perform any specific initialization.
     */
    public UserInserter() {
        // Empty constructor, nothing needs to be done here
    }

    /**
     * Inserts a new user with a hashed password into the users table in the database.
     *
     * @param username the username of the new user
     * @param plainPassword the plain text password to be hashed before insertion
     */
    public static void insertUser(String username, String plainPassword) {
        String dbUrl = "jdbc:sqlite:stores.sqlite"; 
        String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("User inserted successfully with hashed password.");
            } else {
                System.out.println("Failed to insert user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method for testing the insertUser method.
     *
     * @param args commandline arguments (not used)
     */
    public static void main(String[] args) {
        insertUser("domcash97", "Eclipse!");
    }
}