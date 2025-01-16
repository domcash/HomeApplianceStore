//Dominic Cash
//16042439

/**
 * The HomeAppliance class represents an appliance in a home appliance store.
 * It contains details such as SKU, description, category, and price of the appliance.
 * @author dominic cash
 */
public class HomeAppliance {

    private int id;
    private String sku;
    private String description;
    private String category;
    private int price;
    

    /**
     * Constructs a new HomeAppliance object with the specified details.
     *
     * @param sku the unique stock keeping unit (SKU) of the appliance
     * @param description a brief description of the appliance
     * @param category the category under which the appliance falls
     * @param price the price of the appliance in integer format
     */
    public  HomeAppliance (String sku, String description, String category, int price) {
        this.sku = sku;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    /**
     * Constructs a new HomeAppliance object with the specified details, including the appliance ID.
     *
     * @param id the unique ID of the appliance
     * @param sku the unique stock keeping unit (SKU) of the appliance
     * @param description a brief description of the appliance
     * @param category the category under which the appliance falls
     * @param price the price of the appliance in integer format
     */
    public HomeAppliance(int id, String sku, String description, String category, int price) {
        this.id = id;
        this.sku = sku;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    /**
     * Gets the unique ID of the appliance.
     *
     * @return the ID of the appliance
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the appliance.
     *
     * @param id the ID to set for the appliance
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the SKU (Stock Keeping Unit) of the appliance.
     *
     * @return the SKU of the appliance
     */
    public String getSku() {
        return sku;
    }

    /**
     * Sets the SKU (Stock Keeping Unit) of the appliance.
     *
     * @param sku the SKU to set for the appliance
     */
    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * Gets the description of the appliance.
     *
     * @return the description of the appliance
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the appliance.
     *
     * @param description the description to set for the appliance
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the category of the appliance.
     *
     * @return the category of the appliance
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the appliance.
     *
     * @param category the category to set for the appliance
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the price of the appliance.
     *
     * @return the price of the appliance
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets the price of the appliance.
     *
     * @param price the price to set for the appliance
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Returns a string representation of the HomeAppliance object, including its ID, SKU, description,
     * category, and price.
     *
     * @return a string representation of the HomeAppliance object
     */
    @Override
    public String toString() { //Method to convert the object to a string
        return "HomeAppliance{" + "id=" + id + ", sku='" + sku + '\'' + ", description='" + description
                + '\'' + ", category='" + category + '\'' + ", price=" + price + '}';
    }
}
