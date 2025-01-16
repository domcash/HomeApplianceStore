import static org.junit.jupiter.api.Assertions.*;	
import org.junit.jupiter.api.Test;
import org.mockito.Mockito.*;

//Dominic Cash
//16042439

class HomeApplianceTest {

    @Test
    void testConstructorAndGetters() {
        HomeAppliance appliance = new HomeAppliance(1, "SKU123", "Washing Machine", "Laundry", 500);

        assertEquals(1, appliance.getId());
        assertEquals("SKU123", appliance.getSku());
        assertEquals("Washing Machine", appliance.getDescription());
        assertEquals("Laundry", appliance.getCategory());
        assertEquals(500, appliance.getPrice());
    }

    @Test
    void testSetters() {
        HomeAppliance appliance = new HomeAppliance("SKU001", "Refrigerator", "Kitchen", 1000);

        appliance.setId(2);
        appliance.setSku("SKU002");
        appliance.setDescription("Dishwasher");
        appliance.setCategory("Kitchen Appliances");
        appliance.setPrice(750);

        assertEquals(2, appliance.getId());
        assertEquals("SKU002", appliance.getSku());
        assertEquals("Dishwasher", appliance.getDescription());
        assertEquals("Kitchen Appliances", appliance.getCategory());
        assertEquals(750, appliance.getPrice());
    }

    @Test
    void testToString() {
        HomeAppliance appliance = new HomeAppliance(3, "SKU555", "Microwave", "Electronics", 200);

        String expected = "HomeAppliance{id=3, sku='SKU555', description='Microwave', category='Electronics', price=200}";
        assertEquals(expected, appliance.toString());
    }
}