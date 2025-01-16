//Dominic Cash
//16042439

/**
 * Represents a postal address consisting of address lines, country, and postal code.
 * This class provides methods to access and modify the individual components of the address.
 *
 * The address is typically divided into multiple lines (addressLine0, addressLine1, addressLine2),
 * with additional fields for the country and postal code.
 * @author dominic cash
 */
public class Address {
    private String addressLine0;
    private String addressLine1;
    private String addressLine2;
    private String country;
    private String postCode;

    /**
     * Constructs an Address object with the specified components.
     *
     * @param addressLine0 The first line of the address.
     * @param addressLine1 The second line of the address.
     * @param addressLine2 The third line of the address (optional).
     * @param country The country of the address.
     * @param postCode The postal code of the address.
     */
    public Address(String addressLine0, String addressLine1, String addressLine2, String country, String postCode) {
        this.addressLine0 = addressLine0;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.country = country;
        this.postCode = postCode;
    }

    /**
     * Gets the first line of the address.
     *
     * @return The first line of the address.
     */
    public String getAddressLine0() {
        return addressLine0;
    }

    /**
     * Sets the first line of the address.
     *
     * @param addressLine0 The first line of the address.
     */
    public void setAddressLine0(String addressLine0) {
        this.addressLine0 = addressLine0;
    }

    /**
     * Gets the second line of the address.
     *
     * @return The second line of the address.
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Sets the second line of the address.
     *
     * @param addressLine1 The second line of the address.
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * Gets the third line of the address.
     *
     * @return The third line of the address.
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Sets the third line of the address.
     *
     * @param addressLine2 The third line of the address.
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * Gets the country of the address.
     *
     * @return The country of the address.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country of the address.
     *
     * @param country The country of the address.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the postal code of the address.
     *
     * @return The postal code of the address.
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Sets the postal code of the address.
     *
     * @param postCode The postal code of the address.
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * Returns a string representation of the address in a human-readable format.
     *
     * @return A string representing the full address.
     */
    @Override
    public String toString() {
        return addressLine0 + ", " + addressLine1 + ", " + addressLine2 + ", " + country + ", " + postCode;
    }
}

