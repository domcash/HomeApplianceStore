import java.io.UnsupportedEncodingException;	
import java.net.URLDecoder;
import java.util.*;

//Dominic Cash
//16042439

/**
 * The {@code Controller} class serves as the main entry point for the Home Appliance Store application.
 * It provides a menu-driven interface to perform CRUD (Create, Read, Update, Delete) operations on
 * products and customers using DAOs (Data Access Objects).
 *
 * Features include:
 * 
 * Listing all products and customers
 * Searching for a product or customer by ID
 * Adding new products or customers
 * Updating product or customer details
 * Deleting products or customers
 *
 * The program runs in a loop, displaying a menu to the user and handling input via the {@link Scanner} class.
 * @author dominic cash
 */
public class Controller {

    /**
     * The main method that starts the Home Appliance Store application.
     *
     * It initializes DAOs for products and customers, creates a menu-driven interface,
     * and routes user choices to the appropriate methods.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        HomeApplianceDAO applianceDAO = new HomeApplianceDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Home Appliance Store---");
            System.out.println("1. List All Products");
            System.out.println("2. Search for Product by ID");
            System.out.println("3. Add New Product");
            System.out.println("4. Update Product by ID");
            System.out.println("5. Delete Product by ID");
            System.out.println("6. List All Customers");
            System.out.println("7. Search for Customer by ID");
            System.out.println("8. Add New Customer");
            System.out.println("9. Update Customer by ID");
            System.out.println("10. Delete Customer by ID");
            System.out.println("11. Exit");
            System.out.print("Enter your choice: ");

            String choice = in.nextLine();

            switch (choice) {
                case "1":
                    listAllProducts(applianceDAO);
                    break;
                case "2":
                    searchProductById(applianceDAO, in);
                    break;
                case "3":
                    addNewProduct(applianceDAO, in);
                    break;
                case "4":
                    updateProductById(applianceDAO, in);
                    break;
                case "5":
                    deleteProductById(applianceDAO, in);
                    break;
                case "6":
                    listAllCustomers(customerDAO);
                    break;
                case "7":
                    searchCustomerByID(customerDAO, in);
                    break;
                case "8":
                    addNewCustomer(customerDAO, in);
                    break;
                case "9":
                    updateByCustomerID(customerDAO, in);
                    break;
                case "10":
                    deleteCustomerById(customerDAO, in);
                    break;
                case "11":
                    System.out.println("Exiting program...");
                    in.close();
                    return; 
                default:
                    System.out.println("Invalid choice. Please enter numbers 1-11");
            }
        }
    }


    /**
     * Displays all products available in the store by fetching them from the database.
     *
     * @param applianceDAO the DAO used to access product data
     */
    public static void listAllProducts(HomeApplianceDAO applianceDAO) {
        System.out.println("\n--- List All Products ---");
        List<HomeAppliance> appliances = applianceDAO.findAllProducts();

        if (appliances.isEmpty()) {
            System.out.println("No products found.");
        } else {
            appliances.forEach(appliance -> System.out.println(appliance));
        }
    }


    /**
     * Searches for a product in the store by its ID and displays the details if found.
     *
     * @param applianceDAO the DAO used to access product data
     * @param in the {@link Scanner} object used to read user input
     */
    public static void searchProductById(HomeApplianceDAO applianceDAO, Scanner in) {
        System.out.println("\n--- Search for Product by ID ---");
        System.out.print("Enter the ID of the product: ");

        int id = -1;
        while (true) {
            try {
                String input = in.nextLine();
                id = Integer.parseInt(input);
                break; 
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric ID.");
            }
        }

        HomeAppliance appliance = applianceDAO.findProduct(id);
        if (appliance != null) {
            System.out.println("Product found: " + appliance);
        } else {
            System.out.println("Product not found with ID: " + id);
        }
    }

    /**
     * Adds a new product to the store by taking user input for its details.
     *
     * @param applianceDAO the DAO used to access and modify product data
     * @param in the {@link Scanner} object used to read user input
     */
    public static void addNewProduct(HomeApplianceDAO applianceDAO, Scanner in) {
        System.out.println("\n--- Add New Product ---");

        System.out.print("Enter SKU (e.g., HA100): ");
        String sku = in.nextLine();
        while (sku.isEmpty()) {
            System.out.print("SKU cannot be blank. Please enter SKU: ");
            sku = in.nextLine();
        }

        System.out.print("Enter Description (e.g., Washing Machine): ");
        String description = in.nextLine();
        while (description.isEmpty()) {
            System.out.print("Description cannot be blank. Please enter Description: ");
            description = in.nextLine();
        }

        System.out.print("Enter Category (e.g., Kitchen, Electronics, etc.): ");
        String category = in.nextLine();
        while (category.isEmpty()) {
            System.out.print("Category cannot be blank. Please enter Category: ");
            category = in.nextLine();
        }

        int price = 0;
        while (true) {
            System.out.print("Enter Price (e.g., 150): ");
            String priceInput = in.nextLine();
            try {
                price = Integer.parseInt(priceInput);
                if (price < 0) {
                    System.out.println("Price cannot be negative. Please enter a valid price.");
                } else {
                    break; 
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric price.");
            }
        }

        HomeAppliance newProduct = new HomeAppliance(sku, description, category, price);
        boolean success = applianceDAO.insertItem(newProduct);

        if (success) {
            System.out.println("Product added successfully!");
            System.out.println("Product Details: " + newProduct);
        } else {
            System.out.println("Failed to add product. Please try again.");
        }
    }

    /**
     * Updates the details of an existing product in the store by its ID.
     *
     * The user can choose to update specific fields or keep existing values.
     *
     * @param applianceDAO the DAO used to access and modify product data
     * @param in the {@link Scanner} object used to read user input
     */
    public static void updateProductById(HomeApplianceDAO applianceDAO, Scanner in) {
        System.out.println("\n--- Update Product by ID ---");

        System.out.print("Enter the ID of the product to update: ");
        int updateId = -1;

        while (true) {
            try {
                String input = in.nextLine();
                updateId = Integer.parseInt(input);
                System.out.println("You have entered ID: " + updateId);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric ID.");
            }
        }

        HomeAppliance existingProduct = applianceDAO.findProduct(updateId);
        if (existingProduct == null) {
            System.out.println("Product not found with ID: " + updateId);
            return;
        }

        System.out.println("Current Details: " + existingProduct);
        System.out.println("Enter new details for the product (leave blank to keep current value):");

        System.out.print("Enter new SKU (current: " + existingProduct.getSku() + "): ");
        String newSku = in.nextLine();
        if (!newSku.isEmpty()) existingProduct.setSku(newSku);

        System.out.print("Enter new description (current: " + existingProduct.getDescription() + "): ");
        String newDescription = in.nextLine();
        if (!newDescription.isEmpty()) existingProduct.setDescription(newDescription);

        System.out.print("Enter new category (current: " + existingProduct.getCategory() + "): ");
        String newCategory = in.nextLine();
        if (!newCategory.isEmpty()) existingProduct.setCategory(newCategory);

        int newPrice = existingProduct.getPrice();
        while (true) {
            System.out.print("Enter new price (current: " + existingProduct.getPrice() + "): ");
            String priceInput = in.nextLine();

            if (priceInput.isEmpty()) break;

            try {
                newPrice = Integer.parseInt(priceInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric price or leave it blank to keep the current value.");
            }
        }

        existingProduct.setPrice(newPrice);
        boolean success = applianceDAO.updateItem(existingProduct);

        if (success) {
            System.out.println("Product updated successfully!");
            System.out.println("Updated Details: " + existingProduct);
        } else {
            System.out.println("Failed to update product. Please try again.");
        }
    }

    /**
     * Deletes a product from the store by its ID after user confirmation.
     *
     * @param applianceDAO the DAO used to access and modify product data
     * @param in the {@link Scanner} object used to read user input
     */
    public static void deleteProductById(HomeApplianceDAO applianceDAO, Scanner in) {
        System.out.println("\n--- Delete Product by ID ---");
        System.out.print("Enter the ID of the product to delete: ");

        int deleteId = -1;
        while (true) {
            try {
                String input = in.nextLine();
                deleteId = Integer.parseInt(input);
                break; // Valid ID entered
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric ID.");
            }
        }

        HomeAppliance appliance = applianceDAO.findProduct(deleteId);
        if (appliance == null) {
            System.out.println("Product not found with ID: " + deleteId);
            return;
        }

        System.out.println("You have selected the following product for deletion:");
        System.out.println(appliance);
        System.out.print("Are you sure you want to delete this product? (yes/no): ");
        String confirmation = in.nextLine().trim().toLowerCase();

        int finalDeleteId = deleteId;
        Runnable deleteAction = () -> {
            boolean success = applianceDAO.deleteItem(finalDeleteId);
            if (success) {
                System.out.println("Product with ID " + finalDeleteId + " deleted successfully.");
            } else {
                System.out.println("Failed to delete the product. Please try again.");
            }
        };

        if (confirmation.equals("yes")) {
            deleteAction.run();
        } else if (confirmation.equals("no")) {
            System.out.println("Product deletion cancelled.");
        } else {
            System.out.println("Invalid input. Product deletion cancelled.");
        }

    }

    /**
     * Lists all customers in the system.
     *
     * @param customerDAO The DAO object used to retrieve customer data.
     */
    public static void listAllCustomers(CustomerDAO customerDAO) {
        System.out.println("\n--- List All Customers ---");
        List<Customer> customers = customerDAO.findAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
        } else {
            customers.forEach(customer -> System.out.println(customer));
        }
    }

    /**
     * Searches for a customer by their ID.
     *
     * @param customerDAO The DAO object used to retrieve customer data.
     * @param in A Scanner object for user input.
     */
    public static void searchCustomerByID(CustomerDAO customerDAO, Scanner in) {
        System.out.println("\n--- Search Customer by ID ---");
        System.out.print("Enter ID of the customer: ");

        int id = -1;
        while (true) {
            try {
                String input = in.nextLine();
                id = Integer.parseInt(input);
                break; 
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric ID.");
            }
        }

        Customer existingCustomer = customerDAO.findCustomer(id);
        if (existingCustomer != null) {
            System.out.println("Customer found: " + existingCustomer);
        } else {
            System.out.println("Customer not found with ID: " + id + ". Please try again.");

        }
    }

    /**
     * Adds a new customer to the system.
     *
     * @param customerDAO The DAO object used to add customer data.
     * @param in A Scanner object for user input.
     */
    public static void addNewCustomer(CustomerDAO customerDAO, Scanner in) {
        System.out.println("\n--- Add New Customer ---");
        System.out.print("Enter Business Name (e.g., Greggs): ");
        String businessName = in.nextLine();
        while (businessName.isEmpty()) {
            System.out.print("Business Name cannot be blank. Please enter Business Name");
            businessName = in.nextLine();
        }
        
        System.out.print("Enter Address Line 1 ");
        String addressLine0 = in.nextLine();
        while (addressLine0.isEmpty()) {
            System.out.print("Address Line 1 cannot be blank. Please enter Address Line 1");
            addressLine0 = in.nextLine();
        }

        System.out.print("Enter Address Line 2 ");
        String addressLine1 = in.nextLine();
        while (addressLine1.isEmpty()) {
            System.out.print("Address Line 2 cannot be blank. Please enter Address Line 1");
            addressLine1 = in.nextLine();
        }

        System.out.print("Enter Address Line 3 ");
        String addressLine2 = in.nextLine();
        while (addressLine2.isEmpty()) {
            System.out.print("Address Line 3 cannot be blank. Please enter Address Line 1");
            addressLine2 = in.nextLine();
        }

        System.out.print("Enter Country ");
        String country = in.nextLine();
        while (country.isEmpty()) {
            System.out.print("Country cannot be blank. Please enter Country");
            country = in.nextLine();
        }

        System.out.print("Enter Postal Code ");
        String postCode = in.nextLine();
        while (postCode.isEmpty()) {
            System.out.print("Postal code cannot be blank. Please enter Postal code");
            postCode = in.nextLine();
        }

        System.out.print("Enter Telephone Number: ");
        String telephoneNumber = in.nextLine();
        while (telephoneNumber.isEmpty()) {
            System.out.print("Telephone Number cannot be blank. Please enter Telephone Number: ");
            telephoneNumber = in.nextLine();
        }

        System.out.print("Enter Email: ");
        String email = in.nextLine();
        while (email.isEmpty()) {
            System.out.print("Email cannot be blank. Please enter Email: ");
            email = in.nextLine();
        }

        Address address = new Address(addressLine0, addressLine1, addressLine2, country, postCode);
        Customer newCustomer = new Customer(businessName, address, telephoneNumber, email);

        boolean success = customerDAO.insertCustomer(newCustomer);
        if (success) {
            System.out.println("Customer added successfully!");
            System.out.println("Customer Details: " + newCustomer);
        } else {
            System.out.println("Failed to add customer. Please try again.");
        }
    }

    /**
     * Updates an existing customer by their ID.
     *
     * @param customerDAO The DAO object used to update customer data.
     * @param in A Scanner object for user input.
     */
    public static void updateByCustomerID(CustomerDAO customerDAO, Scanner in) {

        System.out.println("\n--- Update Customer by ID ---");

        int updateId = -1;
        while (true) {
            try {
                System.out.print("Enter the ID of the customer to update: ");
                String input = in.nextLine();
                updateId = Integer.parseInt(input);
                if (updateId <= 0) {
                    System.out.println("ID must be a positive number. Please try again.");
                    continue;
                }
                Customer existingCustomer = customerDAO.findCustomer(updateId);
                if (existingCustomer != null) {
                    System.out.println("Customer found with ID: " + updateId);
                    break;
                } else {
                    System.out.println("Customer not found with ID: " + updateId + ". Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric ID.");
            }
        }


        Customer existingCustomer = customerDAO.findCustomer(updateId);
        if (existingCustomer == null) {
            System.out.println("Customer not found with ID: " + updateId);
            return;
        }

        System.out.println("Current Details: " + existingCustomer);
        System.out.println("Enter new details for the customer (leave blank to keep current value):");

        System.out.print("Enter new Business Name (current: " + existingCustomer.getBusinessName() + "): ");
        String newBusinessName = in.nextLine();
        if (!newBusinessName.isEmpty()) existingCustomer.setBusinessName(newBusinessName);

        System.out.print("Enter new Address Line 1 (current: " + existingCustomer.getAddress().getAddressLine0() + "): ");
        String newAddressLine0 = in.nextLine();
        if (!newAddressLine0.isEmpty()) {
            existingCustomer.getAddress().setAddressLine0(newAddressLine0);
        }

        System.out.print("Enter new Address Line 2 (current: " + existingCustomer.getAddress().getAddressLine1() + "): ");
        String newAddressLine1 = in.nextLine();
        if (!newAddressLine1.isEmpty()) {
            existingCustomer.getAddress().setAddressLine1(newAddressLine1);
        }

        System.out.print("Enter new Address Line 3 (current: " + existingCustomer.getAddress().getAddressLine2() + "): ");
        String newAddressLine2 = in.nextLine();
        if (!newAddressLine2.isEmpty()) {
            existingCustomer.getAddress().setAddressLine2(newAddressLine2);
        }

        System.out.print("Enter new Country (current: " + existingCustomer.getAddress().getCountry() + "): ");
        String newCountry = in.nextLine();
        if (!newCountry.isEmpty()) {
            existingCustomer.getAddress().setCountry(newCountry);
        }

        System.out.print("Enter new Postal Code (current: " + existingCustomer.getAddress().getPostCode() + "): ");
        String newPostCode = in.nextLine();
        if (!newPostCode.isEmpty()) {
            existingCustomer.getAddress().setPostCode(newPostCode);
        }

        System.out.print("Enter new Telephone Number (current: " + existingCustomer.getTelephoneNumber() + "): ");
        String newNumber = in.nextLine();
        if (!newNumber.isEmpty()) {
            existingCustomer.setTelephoneNumber(newNumber);
        }

        System.out.print("Enter new Email (current: " + existingCustomer.getEmail() + "): ");
        String newEmail = in.nextLine();
        if (!newEmail.isEmpty()) {
            existingCustomer.setEmail(newEmail);
        }


        boolean success = customerDAO.updateCustomer(existingCustomer);

        if (success) {
            System.out.println("Customer updated successfully!");
            System.out.println("Updated Details: " + existingCustomer);
        } else {
            System.out.println("Failed to update customer. Please try again.");

        }
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param customerDAO The DAO object used to delete customer data.
     * @param in A Scanner object for user input.
     */
    public static void deleteCustomerById(CustomerDAO customerDAO, Scanner in) {
        System.out.println("\n--- Delete Customer by ID ---");
        System.out.print("Enter the ID of the customer to delete: ");

        int deleteId = -1;
        while (true) {
            try {
                String input = in.nextLine();
                deleteId = Integer.parseInt(input);
                break; 
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric ID.");
            }
        }

        Customer existingCustomer = customerDAO.findCustomer(deleteId);
        if (existingCustomer == null) {
            System.out.println("Product not found with ID: " + deleteId);
            return;
        }

        System.out.println("You have selected the following customer for deletion:");
        System.out.println(existingCustomer);
        System.out.print("Are you sure you want to delete this customer? (yes/no): ");
        String confirmation = in.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            boolean success = customerDAO.deleteCustomer(deleteId);
            if (success) {
                System.out.println("Customer with ID " + deleteId + " deleted successfully.");
            } else {
                System.out.println("Failed to delete the customer. Please try again.");
            }
        } else if (confirmation.equals("no")) {
            System.out.println("Customer deletion cancelled.");
        } else {
            System.out.println("Invalid input. Customer deletion cancelled.");
        }
    }

    /**
     * Parse query parameters from a URL query string or form data.
     *
     * @param query The query string (e.g., "id=1&name=Fridge&category=Kitchen").
     * @return A map of key-value pairs.
     */
    
    public static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        Arrays.stream(query.split("&"))
                .forEach(pair -> {
                    String[] keyValue = pair.split("=", 2);
                    try {
                        String key = URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
                        params.put(key, value);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });
        return params;
    }
}


