import java.util.ArrayList;	
import java.util.List;

//Dominic Cash
//16042439

/**
 * Represents a shopping basket that holds a list of home appliances.
 * Provides methods for adding items, retrieving the list of items, calculating the total price,
 * and clearing the basket.
 */
public class ShoppingBasket {
    private List<HomeAppliance> items;

    /**
     * Initialises an empty shopping basket.
     */
    public ShoppingBasket() {
        this.items = new ArrayList<>();
    }

    /**
     * Adds a home appliance item to the shopping basket.
     * 
     * @param item the home appliance to add
     */
    public void addItem(HomeAppliance item) {
        this.items.add(item);
    }

    /**
     * Retrieves the list of home appliances in the shopping basket.
     * 
     * @return the list of items in the basket
     */
    public List<HomeAppliance> getItems() {
        return items;
    }

    /**
     * Calculates the total price of all items in the shopping basket.
     * 
     * @return the total price of items in the basket
     */
    public int getTotalPrice() {
        int total = 0;
        for (HomeAppliance item : items) {
            total += item.getPrice();
        }
        return total;
    }

    /**
     * Clears all items from the shopping basket.
     */
    public void clearBasket() {
        this.items.clear();
    }
}