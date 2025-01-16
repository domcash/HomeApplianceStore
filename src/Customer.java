
//Dominic Cash
//16042439

/**
 * The {@code Customer} class represents a customer entity with details such as
 * their ID, business name, address, telephone number, and email.
 *
 * This class provides constructors to initialize customer details with or
 * without an ID, as well as getters and setters for each property.
 *
 * The {@code toString} method returns a string representation of the customer object.
 *
 * @author Dominic
 */
public class Customer {

    /** The unique identifier for the customer. */
    private int customerID;

    /** The name of the customer's business. */
    private String businessName;

    /** The address of the customer as an {@link Address} object. */
    private Address address;

    /** The customer's telephone number. */
    private String telephoneNumber;

    /** The customer's email address. */
    private String email;

    /**
     * Constructs a {@code Customer} with all fields, including an ID.
     *
     * @param customerID the unique identifier for the customer
     * @param businessName the name of the customer's business
     * @param address the address of the customer
     * @param telephoneNumber the customer's telephone number
     * @param email the customer's email address
     */
    public Customer(int customerID, String businessName, Address address, String telephoneNumber, String email) {
        this.customerID = customerID;
        this.businessName = businessName;
        this.address = address;
        this.telephoneNumber = telephoneNumber;
        this.email = email;
    }

    /**
     * Constructs a {@code Customer} without an ID, typically used for new customers.
     *
     * @param businessName the name of the customer's business
     * @param address the address of the customer
     * @param telephoneNumber the customer's telephone number
     * @param email the customer's email address
     */
    public Customer(String businessName, Address address, String telephoneNumber, String email) {
        this.businessName = businessName;
        this.address = address;
        this.telephoneNumber = telephoneNumber;
        this.email = email;
    }


    /**
     * Retrieves the customer's unique identifier.
     *
     * @return the customer's ID
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * Sets the customer's unique identifier.
     *
     * @param customerID the new ID for the customer
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * Retrieves the name of the customer's business.
     *
     * @return the customer's business name
     */
    public String getBusinessName() {
        return businessName;
    }

    /**
     * Sets the name of the customer's business.
     *
     * @param businessName the new business name
     */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    /**
     * Retrieves the customer's address.
     *
     * @return the customer's {@link Address}
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the customer's address.
     *
     * @param address the new address for the customer
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Retrieves the customer's telephone number.
     *
     * @return the customer's telephone number
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * Sets the customer's telephone number.
     *
     * @param telephoneNumber the new telephone number
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * Retrieves the customer's email address.
     *
     * @return the customer's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the customer's email address.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string representation of the {@code Customer} object.
     *
     * @return a string in the format:
     * {@code "Customer [ID=..., Name=..., Address=..., Phone=..., Email=...]"}
     */
    @Override
    public String toString() {
        return "Customer [ID=" + customerID + ", Name=" + businessName + ", Address=" + address + ", Phone=" + telephoneNumber + ", Email=" + email + "]";
    }
}


