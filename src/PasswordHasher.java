import org.mindrot.jbcrypt.BCrypt;	
import java.sql.*;

//Dominic Cash
//16042439

/**
 * The {@code PasswordHasher} class provides functionality to hash existing user passwords
 * in the database. It connects to a SQLite database, retrieves the plain text passwords from
 * the users table, hashes them using the BCrypt hashing algorithm, and updates the passwords
 * in the database with the hashed versions.
 * 
 * The program connects to the database and then iterates through the users, updating their
 * passwords with securely hashed versions.
 * 
 *
 * Example usage:
 * PasswordHasher.main(new String[] {});
 *
 * @author Dominic Cash
 */
public class PasswordHasher {

    /**
     * Main method that establishes a connection to the SQLite database and calls the method
     * to hash and update passwords for all users in the database.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:stores.sqlite"; 

        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            if (conn != null) {
                System.out.println("Connected to the database.");
                hashAndUpdatePasswords(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Hashes the existing plain passwords in the database and updates them with the hashed
     * version. This method retrieves all users' IDs and passwords from the users table,
     * hashes the passwords using BCrypt, and updates the database with the new hashed passwords.
     *
     * @param conn the active database connection
     */
    private static void hashAndUpdatePasswords(Connection conn) {
        String selectQuery = "SELECT id, password FROM users";
        String updateQuery = "UPDATE users SET password = ? WHERE id = ?";

        try (Statement selectStmt = conn.createStatement();
             ResultSet rs = selectStmt.executeQuery(selectQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            while (rs.next()) {
                int userId = rs.getInt("id");
                String plainPassword = rs.getString("password");

                String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

                updateStmt.setString(1, hashedPassword);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();

                System.out.println("Password for user ID " + userId + " hashed and updated.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}