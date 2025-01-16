import static org.junit.jupiter.api.Assertions.*;	
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

//Dominic Cash
//16042439

class CustomerTest {

    private Address mockAddress;

    @BeforeEach
    void setUp() {
        mockAddress = Mockito.mock(Address.class);
        Mockito.when(mockAddress.toString()).thenReturn("123 Main St, City, Country");
    }

    @Test
    void testConstructorWithId() {
        int customerID = 1;
        String businessName = "Example Business";
        String telephoneNumber = "07145884738";
        String email = "contact@gmail.com";

        
        Customer customer = new Customer(customerID, businessName, mockAddress, telephoneNumber, email);

        
        assertEquals(customerID, customer.getCustomerID());
        assertEquals(businessName, customer.getBusinessName());
        assertEquals(mockAddress, customer.getAddress());
        assertEquals(telephoneNumber, customer.getTelephoneNumber());
        assertEquals(email, customer.getEmail());
    }

    @Test
    void testConstructorWithoutId() {
        String businessName = "Example Business";
        String telephoneNumber = "07145884738";
        String email = "contact@gmail.com";

        Customer customer = new Customer(businessName, mockAddress, telephoneNumber, email);

        assertEquals(0, customer.getCustomerID()); 
        assertEquals(businessName, customer.getBusinessName());
        assertEquals(mockAddress, customer.getAddress());
        assertEquals(telephoneNumber, customer.getTelephoneNumber());
        assertEquals(email, customer.getEmail());
    }

    @Test
    void testSettersAndGetters() {
        
        Customer customer = new Customer("Example Business", mockAddress, "07145884738", "contact@gmail.com");

        
        customer.setCustomerID(2);
        customer.setBusinessName("New Name");
        Address newAddress = Mockito.mock(Address.class);
        customer.setAddress(newAddress);
        customer.setTelephoneNumber("098-765-4321");
        customer.setEmail("newemail@domain.com");

        // Assert
        assertEquals(2, customer.getCustomerID());
        assertEquals("New Name", customer.getBusinessName());
        assertEquals(newAddress, customer.getAddress());
        assertEquals("098-765-4321", customer.getTelephoneNumber());
        assertEquals("newemail@domain.com", customer.getEmail());
    }

    @Test
    void testToString() {
        
        Customer customer = new Customer(1, "Example Business", mockAddress, "07145884738", "contact@gmail.com");

        
        String result = customer.toString();

       
        String expected = "Customer [ID=1, Name=Example Business, Address=123 Main St, City, Country, Phone=07145884738, Email=contact@gmail.com]";
        assertEquals(expected, result);
    }
}
