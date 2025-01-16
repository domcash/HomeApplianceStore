import org.junit.jupiter.api.BeforeEach;	
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Dominic Cash
//16042439

class CustomerDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private CustomerDAO customerDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerDAO = spy(CustomerDAO.class);
    }

    @Test
    void testFindAllCustomers() throws Exception {
       
        String query = "SELECT * FROM customer";
        when(customerDAO.connect()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false); 
        when(mockResultSet.getInt("customerID")).thenReturn(1);
        when(mockResultSet.getString("businessName")).thenReturn("Example Business");
        when(mockResultSet.getString("addressLine0")).thenReturn("Test Address Line 1");
        when(mockResultSet.getString("addressLine1")).thenReturn("Test Address Line 2");
        when(mockResultSet.getString("addressLine2")).thenReturn("Test Address Line 3");
        when(mockResultSet.getString("country")).thenReturn("England");
        when(mockResultSet.getString("postCode")).thenReturn("M16 7FA");
        when(mockResultSet.getString("telephoneNumber")).thenReturn("0145883947");
        when(mockResultSet.getString("email")).thenReturn("test@gmail.com");

       
        List<Customer> customers = customerDAO.findAllCustomers();

        
        assertEquals(1, customers.size());
        Customer customer = customers.get(0);
        assertEquals(1, customer.getCustomerID());
        assertEquals("Example Business", customer.getBusinessName());
        assertEquals("Test Address Line 1", customer.getAddress().getAddressLine0());
        assertEquals("Test Address Line 2", customer.getAddress().getAddressLine1());
        assertEquals("Test Address Line 3", customer.getAddress().getAddressLine2());
        assertEquals("England", customer.getAddress().getCountry());
        assertEquals("M16 7FA", customer.getAddress().getPostCode());
        assertEquals("0145883947", customer.getTelephoneNumber());
        assertEquals("test@gmail.com", customer.getEmail());
    }

    @Test
    void testFindCustomer() throws Exception {
        
        String query = "SELECT * FROM customer WHERE customerID = ?";
        when(customerDAO.connect()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true); // Simulate customer found
        when(mockResultSet.getInt("customerID")).thenReturn(1);
        when(mockResultSet.getString("businessName")).thenReturn("Example Business");
        when(mockResultSet.getString("addressLine0")).thenReturn("Test Address Line 1");
        when(mockResultSet.getString("addressLine1")).thenReturn("Test Address Line 2");
        when(mockResultSet.getString("addressLine2")).thenReturn("Test Address Line 3");
        when(mockResultSet.getString("country")).thenReturn("England");
        when(mockResultSet.getString("postCode")).thenReturn("M16 7FA");
        when(mockResultSet.getString("telephoneNumber")).thenReturn("0145883947");
        when(mockResultSet.getString("email")).thenReturn("test@gmail.com");

        
        Customer customer = customerDAO.findCustomer(1);

       
        assertNotNull(customer);
        assertEquals(1, customer.getCustomerID());
        assertEquals("Example Business", customer.getBusinessName());
        assertEquals("Test Address Line 1", customer.getAddress().getAddressLine0());
        assertEquals("Test Address Line 2", customer.getAddress().getAddressLine1());
        assertEquals("Test Address Line 3", customer.getAddress().getAddressLine2());
        assertEquals("England", customer.getAddress().getCountry());
        assertEquals("M16 7FA", customer.getAddress().getPostCode());
        assertEquals("0145883947", customer.getTelephoneNumber());
        assertEquals("test@gmail.com", customer.getEmail());
    }

    @Test
    void testInsertCustomer() throws Exception {
        
        String query = "INSERT INTO customer (businessName, addressLine0, addressLine1, addressLine2, country, postCode, telephoneNumber, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        when(customerDAO.connect()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful insertion

        Address address = new Address("Test Address Line 1", "Test Address Line 2", "Test Address Line 3", "England", "M16 7FA");
        Customer customer = new Customer("Example Business", address, "0145883947", "test@gmail.com");

        
        boolean result = customerDAO.insertCustomer(customer);

        
        assertTrue(result);
        verify(mockPreparedStatement, times(1)).setString(1, "Example Business");
        verify(mockPreparedStatement, times(1)).setString(2, "Test Address Line 1");
        verify(mockPreparedStatement, times(1)).setString(3, "Test Address Line 2");
        verify(mockPreparedStatement, times(1)).setString(4, "Test Address Line 3");
        verify(mockPreparedStatement, times(1)).setString(5, "England");
        verify(mockPreparedStatement, times(1)).setString(6, "M16 7FA");
        verify(mockPreparedStatement, times(1)).setString(7, "0145883947");
        verify(mockPreparedStatement, times(1)).setString(8, "test@gmail.com");
    }

    @Test
    void testUpdateCustomer() throws Exception {
        
        String query = "UPDATE customer SET businessName = ?, addressLine0 = ?, addressLine1 = ?, addressLine2 = ?, country = ?, postCode = ?, telephoneNumber = ?, email = ? WHERE customerID = ?";
        when(customerDAO.connect()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        Address address = new Address("Test Address Line 1", "Test Address Line 2", "Test Address Line 3", "England", "M16 7FA");
        Customer customer = new Customer(1, "Example Business Updated", address, "0145883947", "updated@gmail.com");

      
        boolean result = customerDAO.updateCustomer(customer);

        
        assertTrue(result);
        verify(mockPreparedStatement, times(1)).setString(1, "Example Business Updated");
    }

    @Test
    void testDeleteCustomer() throws Exception {
        
        String query = "DELETE FROM customer WHERE customerID = ?";
        when(customerDAO.connect()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful deletion

        
        boolean result = customerDAO.deleteCustomer(1);

        
        assertTrue(result);
        verify(mockPreparedStatement, times(1)).setInt(1, 1);
    }
}