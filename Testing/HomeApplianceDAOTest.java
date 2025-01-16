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
	
class HomeApplianceDAOTest {

    private HomeApplianceDAO homeApplianceDAO;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        homeApplianceDAO = spy(HomeApplianceDAO.class);
        doReturn(mockConnection).when(homeApplianceDAO).connect();
    }

    @Test
    void testFindAllProducts() throws Exception {
        String query = "SELECT * FROM appliance";

        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        
        when(mockResultSet.next()).thenReturn(true, true, false); // Two rows
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("sku")).thenReturn("SKU1", "SKU2");
        when(mockResultSet.getString("description")).thenReturn("Description1", "Description2");
        when(mockResultSet.getString("category")).thenReturn("Category1", "Category2");
        when(mockResultSet.getInt("price")).thenReturn(100, 200);

        List<HomeAppliance> products = homeApplianceDAO.findAllProducts();

        assertEquals(2, products.size());
        assertEquals("SKU1", products.get(0).getSku());
        assertEquals("Description2", products.get(1).getDescription());
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetAllCategories() throws Exception {
        String query = "SELECT DISTINCT category FROM appliance";

        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        
        when(mockResultSet.next()).thenReturn(true, true, false); // Two categories
        when(mockResultSet.getString("category")).thenReturn("Category1", "Category2");

        List<String> categories = homeApplianceDAO.getAllCategories();

        assertEquals(2, categories.size());
        assertTrue(categories.contains("Category1"));
        assertTrue(categories.contains("Category2"));
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        String query = "SELECT * FROM appliance WHERE category = ?";
        String category = "Category1";

        // Mock the behavior of the connection and prepared statement
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Mock the result set
        when(mockResultSet.next()).thenReturn(true, false); // One product
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("sku")).thenReturn("SKU1");
        when(mockResultSet.getString("description")).thenReturn("Description1");
        when(mockResultSet.getString("category")).thenReturn(category);
        when(mockResultSet.getInt("price")).thenReturn(150);

        // Call the method under test
        List<HomeAppliance> products = homeApplianceDAO.getProductsByCategory(category);

        // Verify the results
        assertEquals(1, products.size());
        assertEquals("SKU1", products.get(0).getSku());
        assertEquals("Description1", products.get(0).getDescription());
        assertEquals(category, products.get(0).getCategory());

        // Verify interactions
        verify(mockPreparedStatement).setString(1, category); // Verify parameter binding
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testInsertItem() throws Exception {
        String query = "INSERT INTO appliance (sku, description, category, price) VALUES (?, ?, ?, ?);";

        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Success

        HomeAppliance appliance = new HomeAppliance("SKU1", "Description1", "Category1", 100);
        boolean result = homeApplianceDAO.insertItem(appliance);

        assertTrue(result);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateItem() throws Exception {
        String query = "UPDATE appliance SET sku = ?, description = ?, category = ?, price = ? WHERE id = ?;";

        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Success

        HomeAppliance appliance = new HomeAppliance(1, "SKU1", "Updated Description", "Updated Category", 120);
        boolean result = homeApplianceDAO.updateItem(appliance);

        assertTrue(result);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteItem() throws Exception {
        String query = "DELETE FROM appliance WHERE id = ?;";

        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Success

        int id = 1;
        boolean result = homeApplianceDAO.deleteItem(id);

        assertTrue(result);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
}